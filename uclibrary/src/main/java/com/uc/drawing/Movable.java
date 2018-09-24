package com.uc.drawing;

/**
 * Created by guoho on 2017/11/24.
 */

public interface Movable {
    boolean isMovable();
    void setMovable(boolean movable);
    void moveTo(float x, float y);
    void offset(float offsetX, float offsetY);
}
