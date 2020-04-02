package com.hxx.panoramademo.utils;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static File saveFile(Bitmap bitmap,String path,String picname) {
        bitmap = bitmap;
        File file=new File(path,picname);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }catch (IOException e) {
            return  null;
        }
        return file;
    }
    public static String[] getPaths(String path){
        File file=new File(path);
        String[] paths=null;
        if (file.exists()&&file.isDirectory()){
            File[] files=file.listFiles();
            paths=new String[files.length];
            for (int i=0;i<files.length;i++){
                paths[i]=files[i].getPath();
            }
        }
        return paths;
    }
}
