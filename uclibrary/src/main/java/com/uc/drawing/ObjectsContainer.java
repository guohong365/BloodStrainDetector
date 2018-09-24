package com.uc.drawing;

import java.util.List;

public interface ObjectsContainer extends Rectangle, ObjectsGroup{
    ObjectsGroup getSelectedItems();
    ObjectsGroup getGroup();
    void toFront(DrawObject item);
    void toBack(DrawObject item);
    void toPrior(DrawObject item);
    void toEnd(DrawObject item);
    float getViewScale();
    void setViewScale(float viewScale);
    void setViewScaleToFit();
    void align(List<DrawObject> items, DrawObject reference, AlignMode alignMode);
    void arrange(List<DrawObject> items, DrawObject reference, ArrangeMode arrangeMode);
}
