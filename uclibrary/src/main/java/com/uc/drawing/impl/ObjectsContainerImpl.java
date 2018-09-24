package com.uc.drawing.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.SizeF;

import com.uc.model.Selectable.OnSelectedChangedListener;
import com.uc.model.Selectable;
import com.uc.drawing.AlignMode;
import com.uc.drawing.ArrangeMode;
import com.uc.drawing.DrawObject;
import com.uc.drawing.ObjectsContainer;
import com.uc.drawing.ObjectsGroup;

import java.util.List;

public class ObjectsContainerImpl extends RectangleImpl implements ObjectsContainer {
    private ObjectsGroup group;
    private ObjectsGroup selectedItems;

    @Override
    public ObjectsGroup getSelectedItems() {
        return selectedItems;
    }

    public ObjectsContainerImpl() {
    }

    public ObjectsContainerImpl(PointF position, SizeF size) {
        super(position, size);
    }
    public ObjectsContainerImpl(float x, float y, float width, float heigh){
        super(x, y, width, heigh);
    }

    @Override
    protected void init(){
        super.init();
        selectedItems=new ObjectsGroupImpl();
        group=new ObjectsGroupImpl();
        setShowBorder(false);
        setBorderAppearance(new LineAppearanceImpl(Color.YELLOW, 1, 5));
    }

    @Override
    public ObjectsGroup getGroup() {
        return group;
    }

    @Override
    public void toFront(DrawObject item) {
        getGroup().getItems().remove(item);
        getGroup().add(item);
    }

    @Override
    public void toBack(DrawObject item) {
        getGroup().getItems().remove(item);
        getGroup().add(0, item);
    }

    @Override
    public void toPrior(DrawObject item) {
        int index = getGroup().getItems().indexOf(item);
        if (index == getGroup().getItems().size() - 1) return;
        index++;
        getGroup().remove(item);
        getGroup().add(index, item);
    }

    @Override
    public void toEnd(DrawObject item) {
        int index = getGroup().getItems().indexOf(item);
        if (index == 0) return;
        index--;
        getGroup().remove(item);
        getGroup().add(index, item);
    }

    float viewScale = 1;

    @Override
    public float getViewScale() {
        return viewScale;
    }

    @Override
    public void setViewScale(float viewScale) {
        if (viewScale <= 0) this.viewScale = 1;
        else this.viewScale = viewScale;
    }

    @Override
    public void setViewScaleToFit() {
        SizeF size = getGroup().getSize();
        float sx = getSize().getWidth() / size.getWidth();
        float sy = getSize().getHeight() / size.getHeight();
        viewScale = Math.max(sx, sy);
    }

    @Override
    public void align(List<DrawObject> items, DrawObject reference, AlignMode alignMode) {
        float offset = 0;
        for (DrawObject item : items) {
            switch (alignMode) {
                case LEFT:
                    item.setPosition(reference.getPosition().x, item.getPosition().y);
                    break;
                case TOP:
                    item.setPosition(item.getPosition().x, reference.getPosition().y);
                    break;
                case RIGHT:
                    offset = item.getBounds().right - reference.getBounds().right;
                    item.setPosition(item.getPosition().x + offset, item.getPosition().y);
                    break;
                case BOTTOM:
                    offset = item.getBounds().bottom - reference.getBounds().bottom;
                    item.setPosition(item.getPosition().x, item.getPosition().y + offset);
                    break;
            }
        }
    }

    @Override
    public void arrange(List<DrawObject> items, DrawObject reference, ArrangeMode arrangeMode) {
        //TODO to implemented
    }

    @Override
    public Region getRegion() {
        return null;
    }

    @Override
    public Path getClip() {
        Path path=new Path();
        RectF bounds=getBounds();
        path.addRect(bounds, Path.Direction.CW);
        return path;
    }

    @Override
    public List<DrawObject> getItems() {
        return group.getItems();
    }

    @Override
    public int getCount() {
        return group.getCount();
    }

    @Override
    public ObjectsGroup add(DrawObject item) {
        return add(0, item);
    }

    @Override
    public ObjectsGroup add(int index, DrawObject item) {
        item.setOnSelectedChangedListener(new OnSelectedChangedListener() {
            @Override
            public void selectedChange(Selectable sender) {
                if(selected){
                    selectedItems.add(0, (DrawObject) sender);
                } else {
                    selectedItems.remove((DrawObject) sender);
                }
            }
        });
        return group.add(index, item);
    }

    @Override
    public void remove(DrawObject item) {
        group.remove(item);
    }

    @Override
    public void removeAt(int index) {
        group.removeAt(index);
    }

    @Override
    public void clear() {
        group.clear();
    }

    @Override
    public DrawObject findAt(PointF pt) {
        return group.findAt(pt);
    }

    @Override
    public List<DrawObject> selectAt(PointF pt) {
        return group.selectAt(pt);
    }

    @Override
    public List<DrawObject> selectIn(RectF rc) {
        return group.selectIn(rc);
    }

    @Override
    public void selectAll() {
        group.selectAll();
    }

    @Override
    public void deselectAll() {
        group.deselectAll();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        group.draw(canvas);
    }
}
