package com.uc.drawing.impl;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;

import com.uc.drawing.Appearance;

class BasicAppearance implements Appearance {
    @Override
    public float getTransparent() {
        return transparent;
    }

    @Override
    public void setTransparent(float transparent) {
        this.transparent = transparent;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }


    private float transparent;///透明度
    private @ColorInt int color;///颜色

    protected BasicAppearance() {
        transparent = 1.0f;///透明度
        color = Color.BLACK;///前景颜色
    }

    protected BasicAppearance(@ColorInt int color, float transparent){
        setColor(color);
        setTransparent(transparent);
    }

    @Override
    public Paint create() {
        Paint paint=newPaint();
        onCreatePaint(paint);
        return paint;
    }
    protected Paint newPaint(){
        return new Paint();
    }
    protected void onCreatePaint(Paint paint){
        paint.setAntiAlias(true);
        paint.setColor(getColor());
        paint.setAlpha((int)(255*getTransparent()));

    }
}
