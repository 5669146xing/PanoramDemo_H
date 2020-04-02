package com.hxx.panoramademo.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxx on 2019/5/5.
 */
public class ShootPicControl {

    private List<Float> orientation_list;
    private List<Float> toproll_list;
    private List<Float> bottomroll_list;
    private boolean last_bottom,last_top;

    public boolean getLast_bottom() {
        return last_bottom;
    }

    public void setLast_bottom(boolean last_bottom) {
        this.last_bottom = last_bottom;
    }

    public boolean getLast_top() {
        return last_top;
    }

    public void setLast_top(boolean last_top) {
        this.last_top = last_top;
    }

    //开始拍摄
    public void init(){
        orientation_list=new ArrayList();
        toproll_list=new ArrayList<>();
        bottomroll_list=new ArrayList<>();
    }
    public int getCurrentOrientationPosition(){
        return orientation_list.size();
    }
    public boolean take_orientation_pic(float roll,float b_roll){
        if (Math.abs(b_roll)>88&& Math.abs(b_roll)<92){
            if (orientation_list.size()==0){
                orientation_list.add(roll);
                return true;
            }else {
                if (orientation_list.size()<12){
                    float last_rool=orientation_list.get(orientation_list.size()-1);
                    if (roll<last_rool){
                        roll+=360;
                    }
                    if ((roll-last_rool)>28&&(roll-last_rool)<32){
                        orientation_list.add(roll);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean take_toproll_pic(float a_roll,float b_roll){
        if (Math.abs(b_roll)>133&& Math.abs(b_roll)<136){
            if (toproll_list.size()==0){
                float a_roll_first=orientation_list.get(0);
                if (Math.abs(a_roll-a_roll_first)<2){
                    toproll_list.add(a_roll);
                    return true;
                }
            }else {
                float last_rool=toproll_list.get(toproll_list.size()-1);
                if (a_roll<last_rool){
                    a_roll+=360;
                }
                if ((a_roll-last_rool)>43&&(a_roll-last_rool)<46){
                    toproll_list.add(a_roll);
                    return true;
                }
            }
        }
        return false;
    }
    public boolean take_bottomroll_pic(float a_roll,float b_roll){
        if (Math.abs(b_roll)>43&& Math.abs(b_roll)<46){
            if (bottomroll_list.size()==0){
                float a_roll_first=orientation_list.get(0);
                if (Math.abs(a_roll-a_roll_first)<2){
                    bottomroll_list.add(a_roll);
                    return true;
                }
            }else {
                float last_rool=bottomroll_list.get(bottomroll_list.size()-1);
                if (a_roll<last_rool){
                    a_roll+=360;
                }
                if ((a_roll-last_rool)>43&&(a_roll-last_rool)<46){
                    bottomroll_list.add(a_roll);
                    return true;
                }
            }
        }
        return false;
    }
    public boolean takeLastBottom_pic(float b_roll){
        if (Math.abs(b_roll)>=0&& Math.abs(b_roll)<5){
            setLast_bottom(true);
            return true;
        }
        return false;
    }
    public boolean takeLastTop_pic(float b_roll){
        if (Math.abs(b_roll)>=175&& Math.abs(b_roll)<=180){
            setLast_top(true);
            return true;
        }
        return false;
    }
    public int getCurrentTopPosition(){
        return toproll_list.size();
    }
    public int getCurrentBottomPosition(){
        return bottomroll_list.size();
    }

    public void deleteOrientataionOne(){
        orientation_list.remove(orientation_list.size()-1);
    }
    public void deleteTopOne(){
        toproll_list.remove(toproll_list.size()-1);
    }
    public void deleteBottomOne(){
        bottomroll_list.remove(bottomroll_list.size()-1);
    }

}
