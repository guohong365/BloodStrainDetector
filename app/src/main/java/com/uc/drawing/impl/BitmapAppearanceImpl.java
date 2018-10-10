package com.uc.drawing.impl;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.uc.drawing.BitmapAppearance;

public class BitmapAppearanceImpl extends BasicAppearance implements BitmapAppearance {
    private ColorFilter colorFilter;
    private ColorMatrix colorMatrix;
    private ColorMatrixColorFilter colorMatrixColorFilter;

    @Override
    protected void onCreatePaint(Paint paint) {
        super.onCreatePaint(paint);
        if(getColorFilter()!=null){
            paint.setColorFilter(getColorFilter());
        }
    }

    @Override
    public ColorFilter getColorFilter() {
        return colorFilter;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.colorFilter = colorFilter;
    }

    @Override
    public ColorMatrix getColorMatrix() {
        return colorMatrix;
    }

    @Override
    public void setColorMatrix(ColorMatrix colorMatrix) {
        this.colorMatrix = colorMatrix;
    }

    @Override
    public ColorMatrixColorFilter getColorMatrixColorFilter() {
        return colorMatrixColorFilter;
    }

    @Override
    public void setColorMatrixColorFilter(ColorMatrixColorFilter colorMatrixColorFilter) {
        this.colorMatrixColorFilter = colorMatrixColorFilter;
    }
}
