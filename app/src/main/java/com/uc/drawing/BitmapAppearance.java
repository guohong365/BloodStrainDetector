package com.uc.drawing;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public interface BitmapAppearance extends Appearance{
    ColorMatrix getColorMatrix();
    void setColorMatrix(ColorMatrix colorMatrix);
    ColorFilter getColorFilter();
    void setColorFilter(ColorFilter colorFilter);
    ColorMatrixColorFilter getColorMatrixColorFilter();
    void setColorMatrixColorFilter(ColorMatrixColorFilter colorMatrixColorFilter);
}
