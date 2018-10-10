package com.uc.drawing;

//绘制样式
public interface FillAppearance extends Appearance{
    int getStyle();///线型
    void setStyle(int fillStyle);
}
