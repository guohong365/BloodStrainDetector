package com.uc.drawing.impl;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;

import com.uc.drawing.FillAppearance;

public class FillAppearanceImpl extends BasicAppearance implements FillAppearance {

    int style;

    @Override
    public int getStyle() {
        return style;
    }

    @Override
    public void setStyle(int fillStyle) {
        style=fillStyle;
    }

    @Override
    protected void onCreatePaint(Paint paint) {
        super.onCreatePaint(paint);
        paint.setStyle(Paint.Style.FILL);
    }

    public FillAppearanceImpl(){
        this(Color.TRANSPARENT);
    }
    public FillAppearanceImpl(@ColorInt int color){
        this(color, 1.0f);
    }
    public FillAppearanceImpl(@ColorInt int color, float transparent){
        this(color, transparent, 0);
    }
    public FillAppearanceImpl(@ColorInt int color, float transparent, int style){
        super(color, transparent);
        setStyle(style);
    }

}
