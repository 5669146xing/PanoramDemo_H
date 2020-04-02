package com.hxx.panoramademo.utils;

/**
 * Created by hxx on 2019/5/5.
 */
public class RollCalculateUtils {
    public static RollCalculateUtils rollCalculateUtils;
    private RollCalculateUtils (){

    }

    public static RollCalculateUtils getRollCalculateUtils() {
        if (rollCalculateUtils==null){
            rollCalculateUtils=new RollCalculateUtils();
        }
        return rollCalculateUtils;
    }
    private float old_roll=-500;
    private long old_time=0;
    //确定是否方位固定（方位角在1秒内变化小于1）
    public boolean isRollSteady(float roll){
        if (old_time==0){
            old_time= System.currentTimeMillis();
            old_roll=roll;
            return false;
        }else if (System.currentTimeMillis()-old_time>1200){
                old_time=System.currentTimeMillis();
                if (Math.abs(roll-old_roll)<1){
                    return true;
                }else {
                    old_roll=roll;
                }
        }
        return false;
    }
    private int count=0;
    public boolean isCanTake(boolean iscantake){
        if (iscantake){
            if (count<3){
                count++;
                return false;
            }else {
                return true;
            }
        }else {
            count=0;
        }
        return false;
    }
}
