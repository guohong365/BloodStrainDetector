package com.uc.android.util.camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by guohog on 2017/12/28.
 */

public class CameraHelper {
    private static Camera mCamera=null;
    public static Camera getCameraInstance(int cameraId){
        if(mCamera==null){
            mCamera=Camera.open(cameraId);
        }
        return mCamera;
    }
    public static void release(){
        if(mCamera!=null){
            mCamera.release();
            mCamera=null;
        }
    }

    public static void startPreview(SurfaceHolder holder, float previewRatio, int flashMode){
        if(mCamera==null) return;
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopPreview(){
        mCamera.stopPreview();
    }

}
