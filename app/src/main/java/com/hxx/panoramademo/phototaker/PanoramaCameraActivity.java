package com.hxx.panoramademo.phototaker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.hxx.panoramademo.ImagesStitchUtil;
import com.hxx.panoramademo.PanoramaActivity;
import com.hxx.panoramademo.R;
import com.hxx.panoramademo.phototaker.view.Camera3dView;
import com.hxx.panoramademo.phototaker.view.MyCenterView;
import com.hxx.panoramademo.utils.FileUtils;
import com.hxx.panoramademo.utils.RollCalculateUtils;
import com.hxx.panoramademo.utils.ShootPicControl;
import com.hxx.panoramademo.utils.ThreadPoolProxyFactory;
import com.otaliastudios.cameraview.AspectRatio;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SizeSelector;
import com.otaliastudios.cameraview.SizeSelectors;
import java.io.File;

public class PanoramaCameraActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private MyCenterView myCenterView;
    private float a_roll,b_roll;
    private RollCalculateUtils rollCalculateUtils;
    private ShootPicControl shootPicControl;
    private CameraView cameraView;
    private File cache_file;
    private TextView mTv;
    private Camera3dView myView;
    int i;
    private String TAG=PanoramaCameraActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama);
        mTv= (TextView) findViewById(R.id.tv);
        shootPicControl = new ShootPicControl();
        shootPicControl.init();
        cache_file=new File(getExternalFilesDir("").getAbsolutePath()+File.separator+"camera_session_"+System.currentTimeMillis()/1000);
        if (!cache_file.exists()){
            cache_file.mkdir();
        }
        myView=findViewById(R.id.camera3d_view);
        rollCalculateUtils = RollCalculateUtils.getRollCalculateUtils();
        myCenterView = (MyCenterView) findViewById(R.id.my);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        cameraView = (CameraView) findViewById(R.id.camera_view);
        cameraView.getCameraOptions();
        SizeSelector width = SizeSelectors.maxWidth(728);
        SizeSelector height = SizeSelectors.maxHeight(1024);
        SizeSelector dimensions = SizeSelectors.and(width, height);
        SizeSelector ratio = SizeSelectors.aspectRatio(AspectRatio.of(3, 4), 0);
        final SizeSelector result = SizeSelectors.or(
                SizeSelectors.and(ratio, dimensions), // Try to match both constraints
                ratio, // If none is found, at least try to match the aspect ratio
                SizeSelectors.biggest() // If none is found, take the biggest
        );
        cameraView.setPictureSize(result);
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(final byte[] jpeg) {
                i++;
                final Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                myView.addmaps(a_roll,bitmap,i);
                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        FileUtils.saveFile(bitmap,cache_file.getAbsolutePath(),picname);
                        if (i==30){
                            doPanorama();
                        }
                    }
                };
                ThreadPoolProxyFactory.getNormalThreadPoolProxy().execute(runnable);
                Toast.makeText(getApplicationContext(),"拍摄完成",Toast.LENGTH_SHORT).show();
            }
        });
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    private String picname;
    private int num;
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        cameraView.start();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        a_roll=(float) (Math.round(sensorEvent.values[0] * 100)) / 100;
        b_roll=(float) (Math.round(sensorEvent.values[1] * 100)) / 100;
        Log.i(TAG, "onSensorChanged: " +a_roll+","+b_roll);
        myView.setNowX(a_roll,b_roll);
        if (shootPicControl.getCurrentOrientationPosition()<12){
            if (rollCalculateUtils.isRollSteady(a_roll)){
                if ( shootPicControl.take_orientation_pic(a_roll,b_roll)){
                    mTv.setText("请保持手机竖直且位置固定，水平旋转镜头至下个黄点拍摄");
                    num=shootPicControl.getCurrentOrientationPosition();
                    picname=num+".jpg";
                    cameraView.captureSnapshot();
                    if (shootPicControl.getCurrentOrientationPosition()==12){
                        mTv.setText("请保持手机位置固定，上翻转镜头至45度寻找下一个黄点拍摄");
                        myCenterView.setTopStartRoll(a_roll);
                    }else {
                        myCenterView.setStartRoll(a_roll);
                    }
                }
            }else {
                myCenterView.setRollandCurrerentPos(a_roll,b_roll,shootPicControl.getCurrentOrientationPosition());
            }
        }else if (shootPicControl.getCurrentTopPosition()<8){
            if (rollCalculateUtils.isRollSteady(a_roll)){
                if (shootPicControl.take_toproll_pic(a_roll,b_roll)){
                    mTv.setText("请保持手机位置固定，45度角不变，水平旋转手机寻找下一个黄点");
                    num+=1;
                    picname=num+".jpg";
                    cameraView.captureSnapshot();
                    if (shootPicControl.getCurrentTopPosition()==8){
                        mTv.setText("请保持手机位置固定，下翻转镜头至-45度寻找下一个黄点拍摄");
                        myCenterView.setBottomStartRoll(a_roll);
                    }else {
                        myCenterView.setTopRollandPos(a_roll,1);
                    }
                }
            }else {
                myCenterView.setTopRoll(a_roll,b_roll);
            }
        }else if (shootPicControl.getCurrentBottomPosition()<8){
            if (rollCalculateUtils.isRollSteady(a_roll)){
                mTv.setText("请保持手机位置固定，-45度角不变，水平旋转手机寻找下一个黄点");
                if (shootPicControl.take_bottomroll_pic(a_roll,b_roll)){
                    num+=1;
                    picname=num+".jpg";
                    cameraView.captureSnapshot();
                    myCenterView.setBottomRollandPos(a_roll,1);
                    if (shootPicControl.getCurrentBottomPosition()==8){
                        mTv.setText("请保持手机位置固定，下翻转镜头至水平寻找下一个黄点拍摄");
                        myCenterView.setLastBottomRoll(a_roll);
                    }
                }
            }else {
                myCenterView.setBottomRoll(a_roll,b_roll);
            }
        }else if (!shootPicControl.getLast_bottom()){
            if (rollCalculateUtils.isRollSteady(a_roll)){
                if (shootPicControl.takeLastBottom_pic(b_roll)){
                    picname="30.jpg";
                    cameraView.captureSnapshot();
                    mTv.setText("请保持手机位置固定，上翻转镜头至水平寻找下一个黄点拍摄");
                    myCenterView.setLastTopRoll(b_roll);
                }
            }else {
                myCenterView.setLastBottomRollChange(b_roll);
            }
        }else if (!shootPicControl.getLast_top()){
            if (rollCalculateUtils.isRollSteady(a_roll)){
                if (shootPicControl.takeLastTop_pic(b_roll)){
                    picname="29.jpg";
                    cameraView.captureSnapshot();
                }
            }else {
                myCenterView.setLastTopRollChange(b_roll);
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void finish(View view){
        if (i>=6){
            Toast.makeText(getApplicationContext(),"合成中",Toast.LENGTH_SHORT).show();
            Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    doPanorama();
                }
            };
            ThreadPoolProxyFactory.getNormalThreadPoolProxy().execute(runnable);
        }else {
            Toast.makeText(getApplicationContext(),"请多拍几张",Toast.LENGTH_SHORT).show();
        }
    }

    private void doPanorama() {
        ImagesStitchUtil.StitchImages(FileUtils.getPaths(cache_file.getAbsolutePath()), new ImagesStitchUtil.onStitchResultListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                Log.d(TAG, "onSuccess: ");
                FileUtils.saveFile(bitmap,getExternalFilesDir("").getAbsolutePath(),"panorama.jpg");
                startActivity(new Intent(PanoramaCameraActivity.this, PanoramaActivity.class));
            }

            @Override
            public void onError(String errorMsg) {
                Log.d(TAG, "onError: "+errorMsg);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
        sensorManager.unregisterListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }
}
