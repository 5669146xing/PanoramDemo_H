package com.hxx.panoramademo.phototaker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * 贴图view
 */
public class Camera3dView extends View {

    private  Paint mPaint;
    /* 画布 */
    private Canvas mCanvas;
    /* 触摸时作用在Camera的矩阵 */
    private Matrix mCameraMatrix;
    /* 照相机，用于旋转时钟实现3D效果 */
    private Camera mCamera;
    /* camera绕X轴旋转的角度 */
    private float mCameraRotateX;
    /* camera绕Y轴旋转的角度 */
    private float mCameraRotateY;
    /* camera旋转的最大角度 */
    private float anow_Row=0,bnow_Row;
    private Map<Float,Bitmap> hor_maps;
    private Map<Float,Bitmap> toplean_maps;
    private Map<Float,Bitmap> bottomlean_maps;
    private int top,left,w,h;
    private Rect mSrcRect ,mDestRect;
    private Bitmap lastbottom,lasttop;

    public Camera3dView(Context context) {
        this(context, null);
    }

    public Camera3dView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Camera3dView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCameraMatrix = new Matrix();
        mCamera = new Camera();
        hor_maps=new HashMap<>();
        toplean_maps=new HashMap<>();
        bottomlean_maps=new HashMap<>();
        mSrcRect=new Rect();
        mDestRect=new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        top=dpToPx(80);
        w=dpToPx(270);
        h=dpToPx(360);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 800;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        left=w/2-dpToPx(270)/2;
    }

    public void addmaps(float a_roll, Bitmap bitmap, int i) {
        if (i==1){
            setBackgroundColor(Color.WHITE);
        }
        if (i<=12){
            hor_maps.put(a_roll,bitmap);
        }else if (i<=20){
            toplean_maps.put(a_roll,bitmap);
        }else if (i<=28){
            bottomlean_maps.put(a_roll,bitmap);
        }else if (i==29){
            lastbottom=bitmap;
        }else {
            lasttop=bitmap;
        }
        invalidate();
    }
    private int dpToPx(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f * (dp >= 0 ? 1 : -1));
    }
    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        for(Map.Entry<Float, Bitmap> entry : hor_maps.entrySet()){
            float roll=entry.getKey();
            mCameraRotateY= anow_Row-roll;
            if (Math.abs(mCameraRotateY)>200){
                mCameraRotateY=anow_Row+360-roll;
            }
            if (Math.abs(mCameraRotateY)>65){
                continue;
            }
            Bitmap bitmap=entry.getValue();
            mCameraRotateX=Math.abs(bnow_Row)-90;
            mSrcRect.setEmpty();
            mSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            mDestRect.setEmpty();
            mDestRect.set((int) (left-mCameraRotateY*15), (int) (top+mCameraRotateX*15), (int) (left-mCameraRotateY*15)+w, (int) (top+mCameraRotateX*15)+h);
            setCameraRotate((float) (mCameraRotateX*0.5), (float) (mCameraRotateY*0.5));
            canvas.drawBitmap(entry.getValue(),mSrcRect,mDestRect,mPaint);
            mCanvas.restore();
        }
        for(Map.Entry<Float, Bitmap> entry : toplean_maps.entrySet()){
            float roll=entry.getKey();
            mCameraRotateY= anow_Row-roll;
            if (Math.abs(mCameraRotateY)>200){
                mCameraRotateY=anow_Row+360-roll;
            }
            if (Math.abs(mCameraRotateY)>65){
                continue;
            }
            if (Math.abs(bnow_Row)<70){
                break;
            }
            Bitmap bitmap=entry.getValue();
            mCameraRotateX=Math.abs(bnow_Row)-135;
            mSrcRect.setEmpty();
            mSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            mDestRect.setEmpty();
            mDestRect.set((int) (left-mCameraRotateY*10), (int) (top+mCameraRotateX*17), (int) (left-mCameraRotateY*10)+w, (int) (top+mCameraRotateX*17)+h);
            setCameraRotate((float) (mCameraRotateX*0.5), (float) (mCameraRotateY*0.5));
            canvas.drawBitmap(entry.getValue(),mSrcRect,mDestRect,mPaint);
            mCanvas.restore();
        }
        for(Map.Entry<Float, Bitmap> entry : bottomlean_maps.entrySet()){
            float roll=entry.getKey();
            mCameraRotateY= anow_Row-roll;
            if (Math.abs(mCameraRotateY)>200){
                mCameraRotateY=anow_Row+360-roll;
            }
            if (Math.abs(mCameraRotateY)>65){
                continue;
            }
            Bitmap bitmap=entry.getValue();
            if (Math.abs(bnow_Row)>110){
                continue;
            }
            mCameraRotateX=Math.abs(bnow_Row)-45;
            mSrcRect.setEmpty();
            mSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            mDestRect.setEmpty();
            mDestRect.set((int) (left-mCameraRotateY*10), (int) (top+mCameraRotateX*17), (int) (left-mCameraRotateY*10)+w, (int) (top+mCameraRotateX*17)+h);
            setCameraRotate((float) (mCameraRotateX*0.5), (float) (mCameraRotateY*0.5));
            canvas.drawBitmap(entry.getValue(),mSrcRect,mDestRect,mPaint);
            mCanvas.restore();
        }
        if (lastbottom!=null){
            mCameraRotateX=Math.abs(bnow_Row);
            if (mCameraRotateX<40){
                mSrcRect.setEmpty();
                mSrcRect.set(0, 0, lastbottom.getWidth(), lastbottom.getHeight());
                mDestRect.setEmpty();
                mDestRect.set(left,(int) (top+mCameraRotateX*10), left+w, (int) (top+mCameraRotateX*10)+h);
                setCameraRotate(mCameraRotateX, 0);
                canvas.drawBitmap(lastbottom,mSrcRect,mDestRect,mPaint);
                mCanvas.restore();
            }
        }
        if (lasttop!=null){
            mCameraRotateX=Math.abs(bnow_Row)-180;
            if (Math.abs(mCameraRotateX)<40){
                mSrcRect.setEmpty();
                mSrcRect.set(0, 0, lasttop.getWidth(), lasttop.getHeight());
                mDestRect.setEmpty();
                mDestRect.set(left,(int) (top+mCameraRotateX*10), left+w, (int) (top+mCameraRotateX*10)+h);
                setCameraRotate(mCameraRotateX, 0);
                canvas.drawBitmap(lasttop,mSrcRect,mDestRect,mPaint);
                mCanvas.restore();
            }
        }
    }
    private float lastrow;
    public void setNowX(float a_nowRaw,float b_nowRaw){
        bnow_Row=b_nowRaw;
        anow_Row=a_nowRaw;
        invalidate();
//        if (lastrow==0){
//            lastrow=anow_Row;
//            invalidate();
//        }else {
//            float x= a_nowRaw-lastrow;
//            if (Math.abs(mCameraRotateY)>200){
//                x=a_nowRaw+360-lastrow;
//            }
//            if (Math.abs(x)>1){
//                lastrow=a_nowRaw;
//                invalidate();
//            }
//        }
    }

    /**
     * 设置3D效果，触摸矩阵的相关设置
     * 应用在绘制图形之前，否则无效
     */
    private void setCameraRotate(float x,float y) {
        mCanvas.save();
        mCameraMatrix.reset();
        mCamera.save();
        mCamera.rotateX(x);//绕x轴旋转角度
        mCamera.rotateY(y);//绕y轴旋转角度
        mCamera.getMatrix(mCameraMatrix);//相关属性设置到matrix中
        mCamera.restore();
        //camera在view左上角那个点，故旋转默认是以左上角为中心旋转
        //故在动作之前pre将matrix向左移动getWidth()/2长度，向上移动getHeight()/2长度
        mCameraMatrix.preTranslate(-getWidth() / 2, -getHeight() / 2);
        //在动作之后post再回到原位
        mCameraMatrix.postTranslate(getWidth() / 2, getHeight() / 2);
        mCanvas.concat(mCameraMatrix);//matrix与canvas相关联
    }
}
