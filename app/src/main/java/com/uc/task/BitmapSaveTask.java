package com.uc.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.ImageWriter;
import android.net.Uri;
import android.os.AsyncTask;

import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.uc.android.camera.Exif;
import com.uc.images.helper.BitmapHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapSaveTask extends AsyncTask<Void, Void, Throwable> {
    public static final Bitmap.CompressFormat DEFAULT_FORMAT=Bitmap.CompressFormat.JPEG;
    public static final int DEFAULT_QUALITY=100;
    public static final boolean DEFAULT_AUTO_RELEASE=true;
    public static class BitmapSaveConfig{
        public Bitmap.CompressFormat format;
        public int quality;
        public boolean autoRelease;

        public BitmapSaveConfig(Bitmap.CompressFormat format, int quality, boolean autoRelease){
            this.format=format;
            this.quality=quality;
            this.autoRelease=autoRelease;
        }
        public BitmapSaveConfig(Bitmap.CompressFormat format, int quality){
            this(format, quality, DEFAULT_AUTO_RELEASE);
        }
        public BitmapSaveConfig(Bitmap.CompressFormat format){
            this(format, DEFAULT_QUALITY);
        }
        public BitmapSaveConfig(){
            this(DEFAULT_FORMAT);
        }
    }
    public interface BitmapSaveCallback{
        void onBitmapSaved(Uri outputUri);
        void onSaveFailure(Throwable throwable);
    }

    private final Context context;
    private final Bitmap bitmap;
    private final Uri fileUri;
    private final BitmapSaveConfig config;
    private final BitmapSaveCallback callback;
    public BitmapSaveTask(Context context, Bitmap bitmap, Uri fileUri, BitmapSaveConfig config, BitmapSaveCallback callback){
        this.context = context;
        this.bitmap=bitmap;
        this.fileUri=fileUri;
        if(config!=null){
            this.config=config;
        } else {
            this.config=new BitmapSaveConfig();
        }
        this.callback=callback;
    }
    @Override
    protected Throwable doInBackground(Void... voids) {
        if(bitmap==null || bitmap.isRecycled()) return new NullPointerException("not valid bitmap.");
        try{

            OutputStream output=context.getContentResolver().openOutputStream(fileUri);
            bitmap.compress(config.format, config.quality, output);
            output.flush();
            output.close();
            if(config.autoRelease){
                bitmap.recycle();
            }
            return null;
        } catch (IOException ex){
            ex.printStackTrace();
            if(config.autoRelease){
                bitmap.recycle();
            }
            return ex;
        }
    }

    @Override
    protected void onPostExecute(Throwable throwable) {
        if(callback!=null){
            if(throwable!=null){
                callback.onSaveFailure(throwable);
                return;
            }
            callback.onBitmapSaved(fileUri);
        }
    }
}
