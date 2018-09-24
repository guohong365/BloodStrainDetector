package com.uc.images.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BitmapHelper{
    public static Bitmap dup(Bitmap source) {
        return dup(source, source.getConfig());
    }
    public static Bitmap dup(Bitmap source, Bitmap.Config config){
        Bitmap bitmap=source.copy(config, true);
        Canvas canvas=new Canvas(bitmap);
        canvas.drawBitmap(source, new Rect(0,0, source.getWidth(), source.getHeight()), new Rect(0,0, source.getWidth(), source.getHeight()), new Paint());
        return bitmap;
    }
}
