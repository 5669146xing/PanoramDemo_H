#include <jni.h>
#include <opencv2/opencv.hpp>
#include <opencv2/core/base.hpp>
#import "opencv2/stitching.hpp"
#import "opencv2/imgcodecs.hpp"
#include "Log.h"
#define BORDER_GRAY_LEVEL 0
#include <android/bitmap.h>


extern "C"{
using namespace cv;
using namespace std;
Mat finalMat;
JNIEXPORT jintArray JNICALL
Java_com_hxx_panoramademo_ImagesStitchUtil_stitchImages(JNIEnv *env, jclass type,
                                                        jobjectArray paths) {
    jstring jstr;
    jsize len = env->GetArrayLength(paths);
    vector<Mat> mats;
    for (int i = 0; i < len; i++) {
        jstr = (jstring) env->GetObjectArrayElement(paths, i);
        const char *path = (char *) env->GetStringUTFChars(jstr, 0);
        LOGE("path %s", path);
        Mat mat = imread(path);
//        cvtColor(mat, mat, CV_RGBA2RGB);
        mats.push_back(mat);
    }
    LOGE("开始拼接......");
    Stitcher stitcher = Stitcher::createDefault(false);
    stitcher.setRegistrationResol(0.6);
    stitcher.setPanoConfidenceThresh(1);
    stitcher.setWaveCorrection(false);
    /*=match_conf默认是0.65，我选0.8，选太大了就没特征点啦,0.8都失败了*/
    detail::BestOf2NearestMatcher *matcher = new detail::BestOf2NearestMatcher(false, 0.65f);
    stitcher.setFeaturesMatcher(matcher);
    stitcher.setBundleAdjuster(new detail::BundleAdjusterRay());
    stitcher.setSeamFinder(new detail::NoSeamFinder);
    stitcher.setExposureCompensator(new detail::NoExposureCompensator());//曝光补偿
    //stitcher.setBlender(new detail::FeatherBlender());
    //PlaneWarper*  cw = new PlaneWarper();
    SphericalWarper*  cw = new SphericalWarper();
    //CylindricalWarper*  cw = new CylindricalWarper();
    stitcher.setWarper(cw);
    Stitcher::Status state = stitcher.stitch(mats, finalMat);
    LOGE("拼接结果: %d", state);
    jintArray jint_arr = env->NewIntArray(3);
    jint *elems = env->GetIntArrayElements(jint_arr, NULL);
    elems[0] = state;//状态码
    elems[1] = finalMat.cols;//宽
    elems[2] = finalMat.rows;//高
    if (state == Stitcher::OK){
        LOGE("拼接成功: OK");
    }else{
        LOGE("拼接失败:fail code %d",state);
    }
    env->ReleaseIntArrayElements(jint_arr, elems, 0);
    return jint_arr;
}

void MatToBitmap(JNIEnv *env, Mat &mat, jobject &bitmap, jboolean needPremultiplyAlpha) {
    AndroidBitmapInfo info;
    void *pixels = 0;
    Mat &src = mat;
    try {
        LOGE("nMatToBitmap");
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        LOGE("nMatToBitmap1");

        CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                  info.format == ANDROID_BITMAP_FORMAT_RGB_565);
        LOGE("nMatToBitmap2 :%d  : %d  :%d", src.dims, src.rows, src.cols);

        CV_Assert(src.dims == 2 && info.height == (uint32_t) src.rows &&
                  info.width == (uint32_t) src.cols);
        LOGE("nMatToBitmap3");
        CV_Assert(src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4);
        LOGE("nMatToBitmap4");
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        LOGE("nMatToBitmap5");
        CV_Assert(pixels);
        LOGE("nMatToBitmap6");
        if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
//            Mat tmp(info.height, info.width, CV_8UC3, pixels);
            if (src.type() == CV_8UC1) {
                LOGE("nMatToBitmap: CV_8UC1 -> RGBA_8888");
                cvtColor(src, tmp, COLOR_GRAY2RGBA);
            } else if (src.type() == CV_8UC3) {
                LOGE("nMatToBitmap: CV_8UC3 -> RGBA_8888");
//                cvtColor(src, tmp, COLOR_RGB2RGBA);
//                cvtColor(src, tmp, COLOR_RGB2RGBA);
                cvtColor(src, tmp, COLOR_BGR2RGBA);
//                src.copyTo(tmp);
            } else if (src.type() == CV_8UC4) {
                LOGE("nMatToBitmap: CV_8UC4 -> RGBA_8888");
                if (needPremultiplyAlpha)
                    cvtColor(src, tmp, COLOR_RGBA2mRGBA);
                else
                    src.copyTo(tmp);
            }
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if (src.type() == CV_8UC1) {
                LOGE("nMatToBitmap: CV_8UC1 -> RGB_565");
                cvtColor(src, tmp, COLOR_GRAY2BGR565);
            } else if (src.type() == CV_8UC3) {
                LOGE("nMatToBitmap: CV_8UC3 -> RGB_565");
//                src.copyTo(tmp);
                cvtColor(src, tmp, COLOR_RGB2BGR565);
            } else if (src.type() == CV_8UC4) {
                LOGE("nMatToBitmap: CV_8UC4 -> RGB_565");
                cvtColor(src, tmp, COLOR_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch (const Exception &e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nMatToBitmap catched Exception: %s", e.what());
        jclass je = env->FindClass("org/opencv/core/CvException");
        if (!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nMatToBitmap catched unknown exception (...)");
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return;
    }
}
JNIEXPORT jint JNICALL
Java_com_hxx_panoramademo_ImagesStitchUtil_getBitmap(JNIEnv *env, jclass type, jobject bitmap) {

    if (finalMat.dims != 2){
        return -1;
    }

    MatToBitmap(env,finalMat,bitmap,false);

    return 0;

}
}