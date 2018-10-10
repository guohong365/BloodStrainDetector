package com.uc.drawing;

import android.graphics.PointF;

public interface Line extends DrawObject {
    PointF getEnd();
    void moveStartTo(PointF pos);
    void moveEndTo(PointF pos);
    boolean isShowStartArrow();
    void setShowStartArrow(boolean showStartArrow);
    boolean isShowEndArrow();
    void setShowEndArrow(boolean showStartArrow);
    Appearance getArrowAppearance();
    void setArrowAppearance(Appearance arrowAppearance);
}
