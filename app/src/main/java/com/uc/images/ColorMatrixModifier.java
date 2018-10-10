package com.uc.images;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.yalantis.ucrop.util.FastBitmapDrawable;

public abstract class ColorMatrixModifier extends ColorMatrixImageFilter implements ImageModifier {
    private static final String TAG="ColorMatrixModifier";
    protected ColorMatrixModifier(String title) {
        super(title);
    }

    @Override
    public void applyForPreview(FastBitmapDrawable drawable) {
        Log.d(TAG, "applyForPreview: " + getTitle());
        Log.d(TAG, "applyForPreview: " + dumpMatrix(getColorMatrix().getArray()));
        drawable.setColorFilter(new ColorMatrixColorFilter(getColorMatrix()));
    }
    String dumpMatrix(float[] matrix){
        String temp="[" + matrix.length + "][";
        for(int i=0; i< matrix.length; i++){
            temp += matrix[i] + (i< matrix.length-1  ? "," : "]");
            if (((i+1)%5)==0) temp +="\n";
        }
        return temp;
    }
    @Override
    public void applyTo(FastBitmapDrawable drawable) {
        Log.d(TAG, "applyTo: " + getTitle());
        float[] array=getColorMatrix().getArray();
        Log.d(TAG, "applyTo: array=" + dumpMatrix(array));
        Bitmap original=drawable.getBitmap();
        if(original==null) return;
        Bitmap result=original.copy(original.getConfig(), true);
        Canvas canvas=new Canvas(result);
        Paint paint=new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColorFilter(new ColorMatrixColorFilter(getColorMatrix()));
        canvas.drawBitmap(original, 0, 0, paint);
        drawable.setBitmap(result);
        original.recycle();
    }

}
