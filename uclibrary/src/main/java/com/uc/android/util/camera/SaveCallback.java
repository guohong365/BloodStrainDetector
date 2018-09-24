package com.uc.android.util.camera;

import android.hardware.Camera;

/**
 * Created by guohog on 2017/12/28.
 */

public class SaveCallback implements Camera.PictureCallback {
    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                mSaver.save(data);
                if(mSaveFinishListener!=null){
                    mSaveFinishListener.finished(this);
                }
            }
        };
        new Thread(runnable).start();
    }

    private PictureSaver mSaver;
    private SaveFinishListener mSaveFinishListener;
    public SaveCallback(PictureSaver pictureSaver){
        this.mSaver =pictureSaver;
    }
    public SaveCallback(PictureSaver saver, SaveFinishListener finishListener){
        this.mSaver =saver;
        this.mSaveFinishListener=finishListener;
    }
}
