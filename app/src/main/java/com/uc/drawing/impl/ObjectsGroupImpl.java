package com.uc.drawing.impl;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.SizeF;

import com.uc.drawing.DrawObject;
import com.uc.drawing.ObjectsGroup;

import java.util.ArrayList;
import java.util.List;

public class ObjectsGroupImpl extends AbstractDrawObject implements ObjectsGroup {
    private List<DrawObject> items=new ArrayList<>();

    @Override
    public ObjectsGroup add(DrawObject item) {
        return add(0, item);
    }

    @Override
    public ObjectsGroup add(int index, DrawObject item){
        items.add(index, item);
        return this;
    }

    @Override
    public List<DrawObject> getItems() {
        return items;
    }
    @Override
    public int getCount(){
        return items.size();
    }
    @Override
    public void remove(DrawObject item) {
        items.remove(item);
    }
    @Override
    public void removeAt(int index){
        items.remove(index);
    }
    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public DrawObject findAt(PointF pt) {
        for(DrawObject item : items){
           if(item.hitTest(pt)==0){
               return item;
           }
        }
        return null;
    }

    @Override
    public List<DrawObject> selectAt(PointF pt) {
        List<DrawObject> selected=new ArrayList<>();
        for(DrawObject item : items){
            if(item.hitTest(pt)==0){
                selected.add(item);
            }
        }
        return selected;
    }

    @Override
    public List<DrawObject> selectIn(RectF pt) {
        List<DrawObject> selected=new ArrayList<>();
        for(DrawObject item: items){
            if(pt.contains(item.getBounds())){
                selected.add(item);
            }
        }
        return selected;
    }

    @Override
    public void selectAll() {
        for(DrawObject item : items){
            item.setSelected(true);
        }
    }
    @Override
    public void deselectAll(){
        for (DrawObject item:items){
            item.setSelected(false);
        }
    }
    @Override
    public SizeF getSize() {
        RectF rectF=getBounds();
        return new SizeF(rectF.width(), rectF.height());

    }

    @Override
    public RectF getBounds() {
        RectF rectF;
        if(items.isEmpty()){
            rectF=new RectF();
            rectF.setEmpty();
            return rectF;
        }
        rectF=items.get(0).getBounds();
        for(DrawObject item: items){
            rectF.union(item.getBounds());
        }
        return rectF;
    }

    @Override
    public PointF getCenter() {
        return new PointF(getBounds().centerX(), getBounds().centerY());
    }

    @Override
    public int getHandleCount() {
        return 0;
    }

    @Override
    public PointF getHandle(int handle) {
        return null;
    }

    @Override
    public int moveHandleTo(int handle, PointF pt) {
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(DrawObject item:items){
            item.draw(canvas);
        }
    }

    @Override
    public Region getRegion(){
        Region region=new Region();
        for(DrawObject item:items){
            region.op(item.getRegion(), Region.Op.UNION);
        }
        return region;
    }
}
