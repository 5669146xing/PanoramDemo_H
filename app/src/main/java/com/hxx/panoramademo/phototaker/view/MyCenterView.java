package com.hxx.panoramademo.phototaker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by hxx on 2019/4/28.  //圆球指示器
 */
public class MyCenterView extends View {
    private Paint strokePaint;
    private int width,height;
    private Paint farPaint;
    private Paint centerPaint;
    private Paint big_rollPaint;
    private int far_width=3,center_width=6;
    private float a_roll_des; //方位角
    private float b_roll_des;   //倾斜角
    private boolean isfar,isshow_ceircle,isorientation_center=true;
    private float y_center,y_center_last,first_pic_roll=-1;
    private float x_center,start_roll;
    private int position;
    private static final  int TYPE_ORIENTATON=0;
    private static final  int TYPE_TOPROLL=1;
    private static final  int TYPE_TBOTTOMROLL=2;
    private static final  int TYPE_TOP=3;
    private static final  int TYPE_BOTTOM=4;
    private int draw_width;
    private int real_w;
    private String TAG=MyCenterView.class.getName();
    private boolean ischexiao;

    public MyCenterView(Context context) {
        this(context,null);
    }
    public MyCenterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }
    public MyCenterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        strokePaint=new Paint();
        strokePaint.setColor(Color.parseColor("#DAA520"));
        strokePaint.setStrokeWidth(2);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
        farPaint=new Paint();
        farPaint.setColor(Color.parseColor("#DAA520"));
        farPaint.setAntiAlias(true);
        centerPaint=new Paint();
        centerPaint.setColor(Color.parseColor("#FFFF00"));
        centerPaint.setAntiAlias(true);
        big_rollPaint=new Paint();
        big_rollPaint.setStrokeWidth(20);
        big_rollPaint.setStyle(Paint.Style.STROKE);
        big_rollPaint.setColor(Color.parseColor("#FFFF00"));
        big_rollPaint.setAntiAlias(true);
        paint=new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        draw_width=w-100;
        real_w=draw_width-width/2;
        height = h;
    }

    public void setStartRoll(float start_roll_src){
        if (ischexiao){
            ischexiao=false;
        }
        start_roll=start_roll_src;
        if (first_pic_roll==-1){
            first_pic_roll=start_roll_src;
        }
//        isshow_ceircle=false;
        if (position==0){
            y_center=height/2;
            isfar=true;
            x_center=width/2;
        }else {
            x_center=draw_width;
            y_center=height/2;
        }
        invalidate();
    }
    public void setRollandCurrerentPos(float a_roll_src,float b_roll,int pos){
        a_roll_des=a_roll_src;
        b_roll_des=b_roll;
        position=pos;
        if (pos==0){
            isshow_ceircle=true;
            x_center=width/2;
            isorientation_center=true;
            y_center= Math.abs(b_roll)/180f*height;
            if (y_center>(height/2-30)&&y_center<(height/2+30)){
                isfar=false;
            }else {
                isfar=true;
            }
            if (y_center_last==0){
                y_center_last=y_center;
                invalidate();
            }else if (Math.abs(y_center-y_center_last)>2){
                y_center_last=y_center;
                invalidate();
            }else {
                y_center_last=y_center;
            }
        }else {
            if (ischexiao){
                a_roll_src+=30;
            }
            y_center= Math.abs(b_roll)/180f*height;
            if (y_center>(height/2-30)&&y_center<(height/2+30)){
                isfar=false;
            }else {
                isfar=true;
            }
            if (a_roll_src>start_roll){
                x_center= draw_width-((a_roll_src-start_roll)/30f*real_w);
            }else {
                if (Math.abs(a_roll_src-start_roll)>200){
                    x_center= draw_width-((a_roll_src+360-start_roll)/30f*real_w);
                }else {
                    x_center= draw_width+((start_roll-a_roll_src)/300f*real_w);
                }
            }
            if (x_center<30){
                x_center=30;
            }else if (x_center>width-30){
                x_center=width-30;
            }
            if (x_center<width/2+30&&x_center>width/2-30){
                isorientation_center=true;
            }else {
                isorientation_center=false;
            }
            invalidate();
        }
    }
    private float change_roll; //从水平 换到上面或下面
    public void setTopRoll(float a_roll_src,float b_roll){
        if (position==0){
            if (b_roll<-90){
                y_center=height/2-300+((Math.abs(b_roll)-90)/45*300);
            }else {
                y_center=height/2-300-(90- Math.abs(b_roll))/45*300;
            }
            if (a_roll_src>start_roll){
                x_center= draw_width-((a_roll_src-start_roll)/change_roll*real_w);
            }else {
                if (Math.abs(a_roll_src-start_roll)>200){
                    x_center= draw_width-((a_roll_src+360-start_roll)/change_roll*real_w);
                }else {
                    x_center= draw_width+((start_roll-a_roll_src)/300f*real_w);
                }
            }
            if (x_center<30){
                x_center=30;
            }else if (x_center>width-30){
                x_center=width-30;
            }
            if (x_center<width/2+30&&x_center>width/2-30){
                isorientation_center=true;
            }else {
                isorientation_center=false;
            }
            if (y_center>(height/2-30)&&y_center<(height/2+30)){
                isfar=false;
            }else {
                isfar=true;
            }
            invalidate();
        }else {
            if (ischexiao){
                a_roll_src+=45;
            }
            y_center=(Math.abs(b_roll)-45)/180f*height;
            if (a_roll_src>start_roll){
                x_center= draw_width-((a_roll_src-start_roll)/45f*real_w);
            }else {
                if (Math.abs(a_roll_src-start_roll)>200){
                    x_center= draw_width-((a_roll_src+360-start_roll)/45f*real_w);
                }else {
                    x_center= draw_width+((start_roll-a_roll_src)/300f*real_w);
                }
            }
            if (x_center<30){
                x_center=30;
            }else if (x_center>width-30){
                x_center=width-30;
            }
            if (x_center<width/2+30&&x_center>width/2-30){
                isorientation_center=true;
            }else {
                isorientation_center=false;
            }
            if (y_center>(height/2-30)&&y_center<(height/2+30)){
                isfar=false;
            }else {
                isfar=true;
            }
            invalidate();
        }
    }
    //第一次 top 45
    public void setTopStartRoll(float topstart_roll_src){
        if (ischexiao){
            ischexiao=false;
        }
        float change=Math.abs(topstart_roll_src-first_pic_roll);
        if (change<65){
            change_roll=change;
        }else {
            if (first_pic_roll<topstart_roll_src){
                change_roll=first_pic_roll+360-topstart_roll_src;
            }else {
                change_roll=change;
            }
        }
        position=0;
        start_roll=topstart_roll_src;
        isshow_ceircle=true;
        x_center=draw_width;
        y_center=height/2-300;
        isfar=true;
        invalidate();
    }

    public void setTopRollandPos(float topstart_roll_src,int pos){
        if (ischexiao){
            ischexiao=false;
        }
        position=pos;
        start_roll=topstart_roll_src;
        isshow_ceircle=true;
        x_center=draw_width;
        y_center=height/2;
        isfar=true;
        invalidate();
    }

    //第一次 bottom 45
    public void setBottomStartRoll(float topstart_roll_src){
        if (ischexiao){
            ischexiao=false;
        }
        float change=Math.abs(topstart_roll_src-first_pic_roll);
        if (change<65){
            change_roll=change;
        }else {
            if (first_pic_roll<topstart_roll_src){
                change_roll=first_pic_roll+360-topstart_roll_src;
            }else {
                change_roll=change;
            }
        }
        position=0;
        start_roll=topstart_roll_src;
        isshow_ceircle=true;
        x_center=draw_width;
        y_center=height/2+300;
        isfar=true;
        invalidate();
    }

    public void setLastBottomRoll(float topstart_roll_src){
        start_roll=topstart_roll_src;
        isshow_ceircle=true;
        isorientation_center=true;
        x_center=width/2;
        y_center=(Math.abs(topstart_roll_src)+90)/180f*height;
        isfar=true;
        invalidate();
    }
    public void setLastTopRoll(float topstart_roll_src){
        start_roll=topstart_roll_src;
        isshow_ceircle=true;
        isorientation_center=true;
        x_center=width/2;
        y_center=height/2-300;
        isfar=true;
        invalidate();
    }
    public void setLastTopRollChange(float b_roll){
        y_center=height/2-300+ Math.abs(b_roll)/180f*300;
        if (y_center>(height/2-30)&&y_center<(height/2+30)){
            isfar=false;
        }else {
            isfar=true;
        }
        invalidate();
    }
    public void setLastBottomRollChange(float b_roll){
        if (Math.abs(b_roll)<80){
            y_center=(Math.abs(b_roll)+90)/180f*height;
        }else {
            y_center=178/180*height;
        }
        if (y_center>(height/2-30)&&y_center<(height/2+30)){
            isfar=false;
        }else {
            isfar=true;
        }
        invalidate();
    }
    public void setBottomRoll(float a_roll_src,float b_roll){
        if (position==0){
            if (b_roll<-135){
                y_center=height/2+300-((Math.abs(b_roll)-135)/90f*300);
            }else {
                y_center=height/2+300-(135- Math.abs(b_roll))/90f*300;
            }
            if (a_roll_src>start_roll){
                x_center= draw_width-((a_roll_src-start_roll)/change_roll*real_w);
            }else {
                if (Math.abs(a_roll_src-start_roll)>200){
                    x_center= draw_width-((a_roll_src+360-start_roll)/change_roll*real_w);
                }else {
                    x_center= draw_width+((start_roll-a_roll_src)/300f*real_w);
                }
            }
            if (x_center<30){
                x_center=30;
            }else if (x_center>width-30){
                x_center=width-30;
            }
            if (x_center<width/2+30&&x_center>width/2-30){
                isorientation_center=true;
            }else {
                isorientation_center=false;
            }
            if (y_center>(height/2-30)&&y_center<(height/2+30)){
                isfar=false;
            }else {
                isfar=true;
            }
            invalidate();
        }else {
            if (ischexiao){
                a_roll_src+=45;
            }
            y_center=(Math.abs(b_roll)+45)/180f*height;
            if (a_roll_src>start_roll){
                x_center= draw_width-((a_roll_src-start_roll)/45f*real_w);
            }else {
                if (Math.abs(a_roll_src-start_roll)>200){
                    x_center= draw_width-((a_roll_src+360-start_roll)/45f*real_w);
                }else {
                    x_center= draw_width+((start_roll-a_roll_src)/300f*real_w);
                }
            }
            if (x_center<30){
                x_center=30;
            }else if (x_center>width-30){
                x_center=width-30;
            }
            if (x_center<width/2+30&&x_center>width/2-30){
                isorientation_center=true;
            }else {
                isorientation_center=false;
            }
            if (y_center>(height/2-30)&&y_center<(height/2+30)){
                isfar=false;
            }else {
                isfar=true;
            }
            invalidate();
        }
    }
    public void setBottomRollandPos(float topstart_roll_src,int pos){
        if (ischexiao){
            ischexiao=false;
        }
        position=pos;
        start_roll=topstart_roll_src;
        isshow_ceircle=true;
        x_center=draw_width;
        y_center=height/2;
        isfar=true;
        invalidate();
    }
    private Paint paint;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,width,height,strokePaint);
        canvas.drawCircle(width/2,height/2,80,big_rollPaint);
        if (isfar||!isorientation_center){
            canvas.drawCircle(x_center,y_center,20,farPaint);
        }else {
            canvas.drawCircle(x_center,y_center,35,centerPaint);
        }
//        canvas.save();
//        canvas.rotate(b_roll_des,width/2,height/2);
//        canvas.drawBitmap(bitmap, width/2-bitmap.getWidth()/2,height/2-bitmap.getHeight()/2,paint);
//        canvas.restore();

    }
    public void chexiao(int type){
        ischexiao=true;
        //中 12张
        if (type==2){
            first_pic_roll=-1;

        }else if (type==3){
            position=3;
        }
    }
}

///**
// * Created by hxx on 2019/4/28.
// */
//public class MyCenterView extends View {
//    private Paint strokePaint;
//    private int width,height;
//    private Paint farPaint;
//    private Paint centerPaint;
//    private Paint big_rollPaint;
//    private int far_width=2,center_width=4;
//    private float a_roll; //方位角
//    private float b_roll;   //倾斜角
//    private boolean isfar,isshow_ceircle,isorientation_center=true;
//    private float y_center,y_center_last;
//    private float x_center,start_roll;
//    private int position;
//    private static final  int TYPE_ORIENTATON=0;
//    private static final  int TYPE_TOPROLL=1;
//    private static final  int TYPE_TBOTTOMROLL=2;
//    private static final  int TYPE_TOP=3;
//    private static final  int TYPE_BOTTOM=4;
//
//    private String TAG=MyCenterView.class.getName();
//    private Map<Double,Integer> pic_map=new HashMap<>();
//    public MyCenterView(Context context) {
//        this(context,null);
//    }
//    public MyCenterView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs,-1);
//    }
//    public MyCenterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        strokePaint=new Paint();
//        strokePaint.setColor(Color.WHITE);
//        strokePaint.setStrokeWidth(2);
//        strokePaint.setStyle(Paint.Style.STROKE);
//        strokePaint.setAntiAlias(true);
//        farPaint=new Paint();
//        farPaint.setColor(Color.parseColor("#DAA520"));
//        farPaint.setAntiAlias(true);
//        centerPaint=new Paint();
//        centerPaint.setColor(Color.parseColor("#FFFF00"));
//        centerPaint.setAntiAlias(true);
//        Resources resources = this.getResources();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        big_rollPaint=new Paint();
//        big_rollPaint.setStrokeWidth(4);
//        big_rollPaint.setStyle(Paint.Style.STROKE);
//        big_rollPaint.setColor(Color.parseColor("#FFFF00"));
//        big_rollPaint.setAntiAlias(true);
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        width = w;
//        height = h;
//    }
//
//    public void setStartRoll(float start_roll_src){
//        start_roll=start_roll_src;
//        isshow_ceircle=false;
//        if (position==0){
//            y_center=height/2-200;
//            isfar=true;
//            x_center=width/2;
//            isshow_ceircle=true;
//        }
//        invalidate();
//    }
//    public void setRollandCurrerentPos(float a_roll_src,float b_roll,int pos){
//        a_roll=a_roll_src;
//        position=pos;
//        if (pos==0){
//            isshow_ceircle=true;
//            x_center=width/2;
//            y_center=Math.abs(b_roll)/180f*height-100;
//            if (y_center>(height/2-200)&&y_center<(height/2+50)){
//                isfar=false;
//            }else {
//                isfar=true;
//            }
//            if (y_center_last==0){
//                y_center_last=y_center;
//                invalidate();
//            }else if (Math.abs(y_center-y_center_last)>2){
//                y_center_last=y_center;
//                invalidate();
//            }else {
//                y_center_last=y_center;
//            }
//        }else {
//            y_center=Math.abs(b_roll)/180f*height-100;
//            if (y_center>(height/2-200)&&y_center<(height/2+50)){
//                isfar=false;
//            }else {
//                isfar=true;
//            }
//            if (a_roll_src>start_roll){
//                isshow_ceircle=a_roll_src-start_roll>=15;
//                x_center= width/2+(200f-((a_roll_src-start_roll)*10-150)/150f*200f);
//            }else {
//                isshow_ceircle=a_roll_src+360-start_roll>=15;
//                x_center= width/2+(200f-((a_roll_src+360-start_roll)-15)/15f*200f);
//            }
//            if (isshow_ceircle){
//                if (x_center<width/2+30){
//                    isorientation_center=true;
//                }else {
//                    isorientation_center=false;
//                }
//                if (x_center>=width/2-20&&x_center<=width/2+220&&isshow_ceircle){
//                    isshow_ceircle=true;
//                }else {
//                    isshow_ceircle=false;
//                }
//            }
//            invalidate();
//        }
//    }
//    public void setTopRoll(float a_roll_src,float b_roll){
//        if (position==0){
//            if (b_roll<-90){
//                y_center=height/2-300+((Math.abs(b_roll)-90)/45*200);
//            }else {
//                y_center=height/2-300-(90-Math.abs(b_roll))/45*200;
//            }
//            if (a_roll_src>start_roll){
//                x_center= width/2+(200f-(a_roll_src-start_roll)/30f*200f);
//            }else {
//                x_center= width/2+(200f-(a_roll_src+360-start_roll)/30f*200f);
//            }
//            if (x_center<0){
//                x_center=-x_center;
//            }
//            if (x_center<width/2+30){
//                isorientation_center=true;
//            }else {
//                isorientation_center=false;
//            }
//            if (y_center>(height/2-200)&&y_center<(height/2+50)){
//                isfar=false;
//            }else {
//                isfar=true;
//            }
//            invalidate();
//        }else {
//            if (b_roll<-135){
//                y_center=height/2-100+((Math.abs(b_roll)-135)/45*100);
//            }else {
//                y_center=height/2-100-(135-Math.abs(b_roll))/45*100;
//            }
//            if (y_center>(height/2-200)&&y_center<(height/2+50)){
//                isfar=false;
//            }else {
//                isfar=true;
//            }
//            if (a_roll_src>start_roll){
//                x_center= width/2+(200f-(a_roll_src-start_roll)/45f*200f);
//            }else {
//                x_center= width/2+(200f-(a_roll_src+360-start_roll)/45f*200f);
//            }
//
//            if (x_center<width/2+30){
//                isorientation_center=true;
//            }else {
//                isorientation_center=false;
//            }
//            invalidate();
//        }
//    }
//    //第一次 top 45
//    public void setTopStartRoll(float topstart_roll_src){
//        position=0;
//        start_roll=topstart_roll_src;
//        isshow_ceircle=true;
//        x_center=width/2+200;
//        y_center=height/2-300;
//        isfar=true;
//        invalidate();
//    }
//
//    //第一次 bottom 45
//    public void setBottomStartRoll(float topstart_roll_src){
//        position=0;
//        start_roll=topstart_roll_src;
//        isshow_ceircle=true;
//        x_center=width/2+200;
//        y_center=height/2+300;
//        isfar=true;
//        invalidate();
//    }
//
//
//    public void setBottomRoll(float a_roll_src,float b_roll){
//        if (position==0){
//            if (b_roll<-135){
//                y_center=height/2+200+((Math.abs(b_roll)-135)/90*300);
//            }else {
//                y_center=height/2+200-(135-Math.abs(b_roll))/90*300;
//            }
//            if (a_roll_src>start_roll){
//                x_center= width/2+(200f-(a_roll_src-start_roll)/45f*200f);
//            }else {
//                x_center= width/2+(200f-(a_roll_src+360-start_roll)/45f*200f);
//            }
//            if (x_center<0){
//                x_center=-x_center;
//            }
//            if (x_center<width/2+30){
//                isorientation_center=true;
//            }else {
//                isorientation_center=false;
//            }
//            if (y_center>(height/2-200)&&y_center<(height/2+50)){
//                isfar=false;
//            }else {
//                isfar=true;
//            }
//            invalidate();
//        }else {
//            if (b_roll<-45){
//                y_center=height/2-100+((Math.abs(b_roll)-45)/45*100);
//            }else {
//                y_center=height/2-100-(45-Math.abs(b_roll))/45*100;
//            }
//            if (y_center>(height/2-200)&&y_center<(height/2+50)){
//                isfar=false;
//            }else {
//                isfar=true;
//            }
//            if (a_roll_src>start_roll){
//                x_center= width/2+(200f-(a_roll_src-start_roll)/45f*200f);
//            }else {
//                x_center= width/2+(200f-(a_roll_src+360-start_roll)/45f*200f);
//            }
//
//            if (x_center<width/2+30){
//                isorientation_center=true;
//            }else {
//                isorientation_center=false;
//            }
//            invalidate();
//        }
//    }
//
//    public void setTopRollandPos(float topstart_roll_src,int pos){
//        position=pos;
//        start_roll=topstart_roll_src;
//        isshow_ceircle=true;
//        x_center=width/2+200;
//        y_center=height/2-100;
//        isfar=true;
//        invalidate();
//    }
//    public void setBottomRollandPos(float topstart_roll_src,int pos){
//        position=pos;
//        start_roll=topstart_roll_src;
//        isshow_ceircle=true;
//        x_center=width/2+200;
//        y_center=height/2-100;
//        isfar=true;
//        invalidate();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        canvas.drawRect(width/2-200,height/2-350,width/2+200,height/2+150,strokePaint);
//        canvas.drawCircle(width/2,height/2-100,50,big_rollPaint);
//        if (isshow_ceircle){
//            if (isfar||!isorientation_center){
//                canvas.drawCircle(x_center,y_center,20,farPaint);
//            }else {
//                canvas.drawCircle(x_center,y_center,20,centerPaint);
//            }
//        }
//
//    }
//}
//
