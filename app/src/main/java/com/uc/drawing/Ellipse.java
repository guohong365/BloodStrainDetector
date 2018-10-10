package com.uc.drawing;

import android.graphics.PointF;

public interface Ellipse extends Rectangle {
    PointF getCenter();
    float getRadiusX();
    void setRadiusX(float radiusX);
    float getRadiusY();
    void setRadiusY(float radiusY);
}
