package com.uc.drawing.impl;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;

import com.uc.drawing.LineAppearance;

public class LineAppearanceImpl extends BasicAppearance implements LineAppearance {
    private int style;
    private float width;
    @Override
    public int getStyle() {
        return style;
    }

    @Override
    public void setStyle(int lineStyle) {
        style=lineStyle;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public void setWidth(float lineWidth) {
        width=lineWidth;
    }

    @Override
    protected void onCreatePaint(Paint paint) {
        super.onCreatePaint(paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getWidth());
    }

    public LineAppearanceImpl(){
        this(Color.BLACK);
    }
    public LineAppearanceImpl(@ColorInt int color){
        this(color, 1.0f);
    }
    public LineAppearanceImpl(@ColorInt int color, float transparent){
        this(color, transparent, 1.0f);
    }
    public LineAppearanceImpl(@ColorInt int color, float transparent, float width){
        this(color, transparent, width, 0);
    }
    public LineAppearanceImpl(@ColorInt int color, float transparent, float width, int style){
        super(color, transparent);
        setWidth(width);
        setStyle(style);
    }
}
