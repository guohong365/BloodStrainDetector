package com.uc.bloodstraindetector.utils;

import android.content.Context;
import android.os.Looper;

import com.bumptech.glide.Glide;

import java.util.logging.Logger;

public class GlideUtils {
    public static void clearDiskCache(final Context context){
        if(Looper.myLooper().equals(Looper.getMainLooper())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(context).clearDiskCache();
                }
            }).start();
        } else {
            Glide.get(context).clearDiskCache();
        }
    }
    public static void clearMemoryCache(final Context context){
        if(Looper.myLooper().equals(Looper.getMainLooper())) {
            Glide.get(context).clearMemory();
        }
    }
    public static void clearAll(final Context context){
        clearDiskCache(context);
        clearMemoryCache(context);
    }
}
