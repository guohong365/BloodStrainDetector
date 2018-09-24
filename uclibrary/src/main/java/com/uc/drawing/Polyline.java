package com.uc.drawing;

import android.graphics.PointF;

public interface Polyline extends DrawObject {
    PointF[] getPoints();
    void setPoints(PointF points);
    void addPoint(PointF point);
    void insertPoint(int pos, PointF point);
    void removePoint(int pos);
}
