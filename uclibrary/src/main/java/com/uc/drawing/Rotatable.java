package com.uc.drawing;

/**
 * Created by guoho on 2017/11/24.
 */

public interface Rotatable {
    boolean isRotatable();
    void setRotatable(boolean rotatable);
    void rotate(float angle);
    void rotate(float angle, float centerX, float centerY);
}
