package com.uc.drawing;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.List;

public interface ObjectsGroup extends DrawObject{
    List<DrawObject> getItems();
    int getCount();
    ObjectsGroup add(DrawObject item);
    ObjectsGroup add(int index, DrawObject item);
    void remove(DrawObject item);
    void removeAt(int index);
    void clear();
    DrawObject findAt(PointF pt);
    List<DrawObject> selectAt(PointF pt);
    List<DrawObject> selectIn(RectF pt);
    void selectAll();
    void deselectAll();
}
