package com.uc.drawing.impl;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.SizeF;

import com.uc.drawing.Rectangle;

public class RectangleImpl extends AbstractDrawObject implements Rectangle {

    boolean roundShow;
    private float width;
    private float height;

    public RectangleImpl(){
        this(new PointF(0,0), new SizeF(1,1));
    }
    public RectangleImpl(float left, float top, float width, float height){
        this(new PointF(left, top), new SizeF(width, height));
    }

    public RectangleImpl(PointF position, SizeF size){
        super(position);
        setWidth(size.getWidth());
        setHeight(size.getHeight());
    }

    @Override
    protected void init() {
        super.init();
        setShowBorder(true);
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public void setWidth(float width) {
        this.width=width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public boolean isRoundShow() {
        return roundShow;
    }

    @Override
    public void setRoundShow(boolean roundShow) {
        this.roundShow=roundShow;
    }
    @Override
    public SizeF getSize() {
        return new SizeF(width, height);
    }

    @Override
    public RectF getBounds() {
        return new RectF(getPosition().x, getPosition().y, getPosition().x + getWidth(), getPosition().y +getHeight());
    }

    @Override
    public Region getRegion() {
        return new Region(0,0, (int)getWidth(), (int)getHeight() );
    }

    @Override
    public PointF getCenter() {
        return new PointF(getPosition().x + getWidth()/2, getPosition().y + getHeight()/2);
    }

    @Override
    public int getHandleCount() {
        return 8;
    }

    @Override
    public PointF getHandle(int handle) {
        switch (handle){
            case 1:
                return new PointF(0,0);
            case 2:
                return new PointF(getWidth()/2, 0);
            case 3:
                return new PointF(getWidth(), 0);
            case 4:
                return new PointF(getWidth(), getHeight()/2);
            case 5:
                return new PointF(getWidth(), getHeight());
            case 6:
                return new PointF(getWidth()/2, getHeight());
            case 7:
                return new PointF(0, getHeight());
            case 8:
                return new PointF(0, getHeight()/2);
        }
        return null;
    }
    @Override
    public int moveHandleTo(int handle, PointF pt){

        return handle;
    }

    @Override
    protected void onDrawBackground(Canvas canvas) {
        Paint paint=getBackgroundAppearance().create();
        canvas.drawRect(0,0, getWidth(), getHeight(), paint);
    }

    @Override
    protected void onDrawBorder(Canvas canvas) {
        Paint paint=getBorderAppearance().create();
        canvas.drawRect(0,0, getWidth(), getHeight(), paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }
}
