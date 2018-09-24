package com.uc.drawing.impl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.SizeF;

import com.uc.drawing.Appearance;
import com.uc.drawing.Line;

public class LineImpl extends AbstractDrawObject implements Line {
    private PointF _end;
    public LineImpl(){
        this(0,0,0,0);
    }
    public LineImpl(float x1, float y1, float x2, float y2){
        this(new PointF(x1,y1),new PointF(x2, y2));
    }
    public LineImpl(PointF start, PointF end){
        setPosition(start);
        _end=new PointF(end.x-start.x, end.y- start.y);
    }

    @Override
    public PointF getEnd() {
        return _end;
    }
    public void setEnd(PointF end){
        _end=end;
    }

    @Override
    public void moveStartTo(PointF pos) {
        setPosition(pos);
    }

    @Override
    public void moveEndTo(PointF pos) {
        setEnd(new PointF(pos.x-getPosition().x, pos.y-getPosition().y));
    }

    public int moveHandleTo(int handle, PointF pt){

        return handle;
    }

    boolean _isShowStartArrow;
    @Override
    public boolean isShowStartArrow() {
        return _isShowStartArrow;
    }

    @Override
    public void setShowStartArrow(boolean showStartArrow) {
        _isShowStartArrow=showStartArrow;
    }

    boolean _isShowEndArrow;
    @Override
    public boolean isShowEndArrow() {
        return _isShowEndArrow;
    }

    @Override
    public void setShowEndArrow(boolean showEndArrow) {
        _isShowEndArrow=showEndArrow;
    }

    Appearance _arrowAppearance;
    @Override
    public Appearance getArrowAppearance() {
        return _arrowAppearance;
    }

    @Override
    public void setArrowAppearance(Appearance arrowAppearance) {
        _arrowAppearance=arrowAppearance;
    }

    @Override
    public SizeF getSize() {
        return new SizeF(Math.abs(getEnd().x), Math.abs(getEnd().y));
    }

    @Override
    public RectF getBounds() {
        SizeF size=getSize();
        float left=getPosition().x + (0 <  getEnd().x ? 0:  getEnd().x);
        float top=getPosition().y + (0 < getEnd().y? 0: getEnd().y);
        return new RectF(left, top, left+ size.getWidth(), top+size.getHeight());
    }

    @Override
    public Region getRegion() {
        Region region=new Region();
        Path path=new Path();
        path.lineTo(_end.x, _end.y);
        region.setPath(path, null);

        return null;
    }

    @Override
    public PointF getCenter() {
        return new PointF((getEnd().x)/2, ( getEnd().y)/2);
    }

    @Override
    public int getHandleCount() {
        return 2;
    }

    @Override
    public PointF getHandle(int handle) {
        switch (handle){
            case 1:
                return LOCAL_ORIGINAL;
            case 2:
                return getEnd();
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint=getAppearance().create();
        canvas.drawLine(0, 0, getEnd().x, getEnd().y, paint);
    }
}
