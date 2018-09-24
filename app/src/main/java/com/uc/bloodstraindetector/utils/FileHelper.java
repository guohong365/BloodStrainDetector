package com.uc.bloodstraindetector.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileHelper extends com.uc.utils.FileHelper {
    private static final String TAG="FileHelper";
    public static final String FILE_PROVIDER="com.uc.bloodstraindetector.FileProvider";


   public static File getImageDir(Context context){
        File file= com.uc.utils.FileHelper.getDir(context, Environment.DIRECTORY_PICTURES);
        if(!file.exists()){
            Log.d(TAG, "getImageDir: picture dir not exists");
            boolean mkdirs = file.mkdirs();
            if(!mkdirs){
                Log.d(TAG, "getImageDir: make picture dir failed.");
            } else {
                Log.d(TAG, "getImageDir: make picture dir successfully.");
            }
        }
        Log.d(TAG, "getImageDir: image dir=" + file.getAbsolutePath());
        return file;
    }

    public static File getImageFile(Context context, String fileName){
        return new File(getImageDir(context), fileName);
    }

    public static Uri getImageUri(Context context, String fileName){
        return com.uc.utils.FileHelper.getFileUri(context,FILE_PROVIDER, Environment.DIRECTORY_PICTURES, fileName);
    }
}
