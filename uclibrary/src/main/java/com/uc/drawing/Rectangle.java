package com.uc.drawing;

/**
 * Created by guoho on 2017/9/28.
 */

public interface Rectangle extends DrawObject {
    float getWidth();
    void setWidth(float width);
    float getHeight();
    void setHeight(float height);
    boolean isRoundShow();
    void setRoundShow(boolean roundShow);
}
