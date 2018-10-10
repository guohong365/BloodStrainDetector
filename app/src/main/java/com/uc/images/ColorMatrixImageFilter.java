package com.uc.images;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public abstract class ColorMatrixImageFilter extends AbstractImageFilter {
    public static final String CATEGORY_COLOR_MATRIX="filter.category.color";
    public static final String OPTION_MATRIX="matrix";
    private ColorMatrix colorMatrix=new ColorMatrix();
    protected ColorMatrixImageFilter(String title) {
        super(CATEGORY_COLOR_MATRIX, title);
    }

    public void setMatrix(float[] colorMatrix) {
        this.colorMatrix.set(colorMatrix);
    }

    public ColorMatrix getColorMatrix() {
        return colorMatrix;
    }

    @Override
    public Bitmap apply(Bitmap input) {
        Bitmap result=input.copy(input.getConfig(), true);
        Canvas canvas=new Canvas(result);
        Paint paint=new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColorFilter(new ColorMatrixColorFilter(getColorMatrix()));
        canvas.drawBitmap(input, 0,0, paint);
        return result;
    }


    @Override
    protected void onSetOption(String name, Object value) {
        if(OPTION_MATRIX.equals(name) && (value instanceof float[])){
            setMatrix((float[]) value);
        }
    }

    @Override
    public boolean isKnownOption(String name) {
        return OPTION_MATRIX.equals(name);
    }

    @Override
    public Object getOption(String name) {
        if(OPTION_MATRIX.equals(name)){
            return getColorMatrix().getArray();
        }
        return null;
    }
}
