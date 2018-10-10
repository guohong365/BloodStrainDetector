package com.uc.drawing;

import android.graphics.Paint;
import android.support.annotation.ColorInt;

//绘制样式
public interface Appearance {
    float getTransparent();///透明度
    void setTransparent(float transparent);
    @ColorInt int getColor();///颜色
    void setColor(@ColorInt int color);
    Paint create();

}
