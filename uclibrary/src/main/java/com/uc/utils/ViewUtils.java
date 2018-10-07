package com.uc.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.PixelCopy;
import android.view.View;

public class ViewUtils {
    public static Bitmap capture(View view){
        Bitmap bitmap=Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        canvas.translate(view.getScrollX(), view.getScrollY());
        view.draw(canvas);
        return bitmap;
    }
}
