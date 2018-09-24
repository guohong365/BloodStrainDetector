package com.uc.drawing.impl;

import android.graphics.Paint;
import android.text.TextPaint;

import com.uc.drawing.TextAppearance;

public class TextApearanceImpl extends BasicAppearance implements TextAppearance {

    @Override
    protected Paint newPaint() {
        return new TextPaint();
    }

    @Override
    protected void onCreatePaint(Paint paint) {
        super.onCreatePaint(paint);
        TextPaint textPaint=(TextPaint)paint;
        textPaint.setTextSize(getFontSize());
        textPaint.setTextAlign(Paint.Align.LEFT);
    }
    float fontSize;
    @Override
    public float getFontSize() {
        return fontSize;
    }

    @Override
    public void setFontSize(float fontSize) {
        this.fontSize=fontSize;
    }
    int fontStyle;
    @Override
    public int getFontStyle() {
        return fontStyle;
    }

    @Override
    public void setFontStyle(int fontStyle) {
        this.fontStyle=fontStyle;
    }
    int textAlignment;
    @Override
    public int getTextAlignment() {
        return textAlignment;
    }

    @Override
    public void setTextAlignment(int textAlignment) {
        this.textAlignment=textAlignment;
    }
    int lineAlignment;
    @Override
    public int getLineAlignment() {
        return lineAlignment;
    }

    @Override
    public void setLineAlignment(int lineAlignment) {
        this.lineAlignment=lineAlignment;
    }
    String fontName;
    @Override
    public String getFontName() {
        return fontName;
    }

    @Override
    public void setFontName(String fontName) {
        this.fontName=fontName;
    }

    boolean verticalText;
    @Override
    public boolean isVerticalText() {
        return verticalText;
    }

    @Override
    public void setVerticalText(boolean verticalText) {
        this.verticalText=verticalText;
    }
}
