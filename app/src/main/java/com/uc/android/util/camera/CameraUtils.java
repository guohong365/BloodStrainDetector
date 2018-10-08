package com.uc.android.util.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.view.Surface;

/**
 * Created by guohog on 2017/12/28.
 */

public class CameraUtils {
    public static void setPreviewOrientation(Activity activity, int cameraId, Camera camera){
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation=activity.getWindowManager().getDefaultDisplay().getRotation();
        int degree=0;
        switch (rotation){
            //case Surface.ROTATION_0:
            case Surface.ROTATION_90:
                degree=90;
                break;
            case Surface.ROTATION_180:
                degree=180;
                break;
            case Surface.ROTATION_270:
                degree=270;
                break;
        }

        int result=(info.orientation-degree + 360)%360;
        camera.setDisplayOrientation(result);
    }
}
