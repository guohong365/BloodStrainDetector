package com.uc.android.view.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by guohog on 2017/12/27.
 */

public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {
    static final String TAG="CAMERA";
    private Camera mCamera;
    private SurfaceHolder mHolder;
    public CameraPreviewView(Context context, Camera camera) {
        super(context);
        mCamera=camera;
        mHolder=getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            mCamera=Camera.open();
        }catch (Exception e){
            Log.e(TAG, "surfaceCreated: " + e.toString() );
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if(mHolder.getSurface()==null) return;
        try{
            mCamera.stopPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.stopPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try{
            mCamera.stopPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
