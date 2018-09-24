package com.uc.drawing;

public interface LineAppearance extends Appearance{
    int getStyle();///线型
    void setStyle(int lineStyle);
    float getWidth();///线宽
    void setWidth(float lineWidth);
}
