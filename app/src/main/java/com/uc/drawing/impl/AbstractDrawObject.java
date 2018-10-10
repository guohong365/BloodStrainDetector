package com.uc.drawing.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.NonNull;

import com.uc.model.AbstractSelectable;
import com.uc.model.Selectable.OnSelectedChangedListener;
import com.uc.model.Selectable;
import com.uc.drawing.Appearance;
import com.uc.drawing.DrawObject;
import com.uc.drawing.FillAppearance;
import com.uc.drawing.LineAppearance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public abstract class AbstractDrawObject extends AbstractSelectable implements DrawObject {
    static final LineAppearance defaultLineAppearance = new LineAppearanceImpl(Color.YELLOW, 1.0f, 0);
    static final FillAppearance defaultTrackAppearance = new FillAppearanceImpl(Color.YELLOW, 0.5f);
    static final FillAppearance defaultBackground = new FillAppearanceImpl(Color.TRANSPARENT, 1.0f, 0);
    private static final float HANDLE_RADIUS = 30;
    private final long id;
    private boolean scalable;
    private boolean showBorder;
    private boolean showTrack;
    private boolean filled;
    private PointF position;
    private PointF rotateCenter;
    private boolean visible;
    private boolean active;
    private boolean rotatable;
    private boolean movable;
    private boolean sizable;
    private DrawObject parent;
    private Map<Integer, List<OnPropertyChangingListener>> changingListenerMap = new HashMap<>();
    private Map<Integer, List<OnPropertyChangedListener>> changedListenerMap = new HashMap<>();
    private Matrix matrix;
    private Appearance backgroundAppearance;
    private Appearance borderAppearance;
    private Appearance trackAppearance;
    private Appearance appearance;

    public AbstractDrawObject() {
        this(new PointF(0, 0));
    }

    protected AbstractDrawObject(PointF position) {
        id= new Random().nextLong();
        init();
        setPosition(position);
    }
    public Long getId(){
        return  id;
    }
    protected void init() {
        active = false;
        movable = true;
        parent = null;
        rotatable = true;
        rotateCenter = new PointF(0, 0);
        scalable = true;
        sizable = true;
        visible = true;
        showBorder = false;
        showTrack = false;
        filled = false;
        matrix = new Matrix();
        setOnSelectedChangedListener(new OnSelectedChangedListener() {
            @Override
            public void selectedChange(Selectable sender) {
                setShowTrack(selected);
            }
        });
    }

    @Override
    public PointF getPosition() {
        return position;
    }

    @Override
    public void setPosition(@NonNull PointF position) {
        this.position = position;
    }
    @Override
    public void setPosition(float x, float y){
        this.position.x=x;
        this.position.y=y;
    }

    @Override
    public PointF getRotateCenter() {
        return rotateCenter;
    }

    @Override
    public void setRotateCenter(@NonNull PointF rotateCenter) {
        this.rotateCenter = rotateCenter;
    }

    @Override
    public Path getClip() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isRotatable() {
        return rotatable;
    }

    @Override
    public void setRotatable(boolean rotatable) {
        this.rotatable = rotatable;
    }

    @Override
    public boolean isMovable() {
        return movable;
    }

    @Override
    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    @Override
    public boolean isSizable() {
        return sizable;
    }

    @Override
    public void setSizable(boolean sizable) {
        this.sizable = sizable;
    }

    @Override
    public DrawObject getParent() {
        return parent;
    }

    @Override
    public void setParent(DrawObject parent) {
        this.parent = parent;
    }

    @Override
    public Appearance getAppearance() {
        return appearance;
    }

    @Override
    public void setAppearance(@NonNull Appearance appearance) {
        this.appearance = appearance;
    }

    @Override
    public void addOnPropertyChangingListener(int id, @NonNull OnPropertyChangingListener listener) {
        if (!changingListenerMap.containsKey(id)) {
            changingListenerMap.put(id, new ArrayList<OnPropertyChangingListener>());
        }
        changingListenerMap.get(id).add(listener);
    }

    @Override
    public void addOnPropertyChangedListener(int id, @NonNull OnPropertyChangedListener listener) {
        if (!changedListenerMap.containsKey(id)) {
            changedListenerMap.put(id, new ArrayList<OnPropertyChangedListener>());
        }
        changedListenerMap.get(id).add(listener);
    }

    @Override
    public void draw(Canvas canvas) {
        if (isVisible()) {
            canvas.save();
            if (getClip() != null) {
                canvas.clipPath(getClip());
            }
            Matrix matrix=new Matrix(getMatrix());
            //matrix.preTranslate(getPosition().x, getPosition().y);
            canvas.translate(getPosition().x, getPosition().y);
            canvas.concat(matrix);
            drawBackground(canvas);
            onDraw(canvas);
            drawBorder(canvas);
            drawTracker(canvas);
            canvas.restore();
        }
    }

    protected abstract void onDraw(Canvas canvas);

    public Appearance getBorderAppearance() {
        if (borderAppearance == null) return defaultLineAppearance;
        return borderAppearance;
    }

    public void setBorderAppearance(Appearance borderAppearance) {
        this.borderAppearance = borderAppearance;
    }

    public Appearance getTrackAppearance() {
        if (trackAppearance == null) return defaultTrackAppearance;
        return trackAppearance;
    }

    public void setTrackAppearance(Appearance trackAppearance) {
        this.trackAppearance = trackAppearance;
    }

    public Appearance getBackgroundAppearance() {
        if (backgroundAppearance == null) return defaultBackground;
        return backgroundAppearance;
    }

    public void setBackgroundAppearance(Appearance appearance) {
        backgroundAppearance = appearance;
    }

    protected void onDrawBorder(Canvas canvas) {
        Paint paint = getBorderAppearance().create();
        canvas.drawRect(0, 0, getSize().getWidth(), getSize().getHeight(), paint);
    }

    protected Path getHandleTrack(int index){
        Path path=new Path();
        path.addCircle(getHandle(index).x, getHandle(index).y, HANDLE_RADIUS, Path.Direction.CW);
        return path;
    }

    protected void onDrawTrack(Canvas canvas) {
        Paint paint = getTrackAppearance().create();
        for (int i = 1; i <= getHandleCount(); i++) {
            Path handle = getHandleTrack(i);
            canvas.drawPath(handle, paint);
        }
    }

    protected void onDrawBackground(Canvas canvas) {

    }

    public void drawBackground(Canvas canvas) {
        if (isFilled()) {
            onDrawBackground(canvas);
        }
    }

    public void drawBorder(Canvas canvas) {
        if (isShowBorder()) {
            onDrawBorder(canvas);
        }
    }

    public void drawTracker(Canvas canvas) {
        if (isShowTrack()) {
            onDrawTrack(canvas);
        }
    }

    @Override
    public void moveTo(float x, float y) {
        setPosition(new PointF(x, y));
    }

    public Matrix getMatrix() {
        return matrix;
    }

    @Override
    public void offset(float offsetX, float offsetY) {
        matrix.postTranslate(offsetX, offsetY);
    }

    @Override
    public void rotate(float angle) {
        matrix.postRotate(angle);
    }

    @Override
    public void rotate(float angle, float centerX, float centerY) {
        matrix.postRotate(angle, centerX, centerY);
    }

    @Override
    public boolean isScalable() {
        return scalable;
    }

    @Override
    public void setScalable(boolean scalable) {
        this.scalable = scalable;
    }

    @Override
    public void scale(float scaleX, float scaleY) {
        matrix.postScale(scaleX, scaleY);
    }

    @Override
    public void scaleAt(float scaleX, float scaleY, float centerX, float centerY) {
        matrix.postScale(scaleX, scaleY, centerX, centerY);
    }

    @Override
    public PointF local2Parent(@NonNull PointF pt) {
        Matrix matrix = new Matrix(getMatrix());
        float[] dest = new float[2];
        matrix.preTranslate(getPosition().x, getPosition().y);
        matrix.mapPoints(dest, new float[]{pt.x, pt.y});
        return new PointF(dest[0], dest[1]);
    }

    public PointF[] local2Parent(@NonNull PointF[] pts) {
        PointF[] ret = new PointF[pts.length];
        for (int i = 0; i < pts.length; i++) {
            ret[i] = local2Parent(pts[i]);
        }
        return ret;
    }

    void Pt2Array(PointF[] pt, float[] dest) {
        for (int i = 0; i < pt.length; i++) {
            dest[i * 2] = pt[i].x;
            dest[i * 2 + 1] = pt[i].y;
        }
    }

    void Array2Pt(float[] src, PointF[] pt) {
        int index = 0;
        for (int i = 0; i < src.length; i += 2) {
            pt[index] = new PointF(src[i], src[i + 1]);
            index++;
        }
    }

    @Override
    public PointF parent2Local(@NonNull PointF pt) {
        Matrix matrix = new Matrix(getMatrix());
        matrix.preTranslate(getPosition().x, getPosition().y);
        matrix.invert(matrix);
        float[] dest = new float[2];
        matrix.mapPoints(dest, new float[]{pt.x, pt.y});
        return new PointF(dest[0], dest[1]);
    }

    @Override
    public PointF[] parent2Local(@NonNull PointF[] pts) {
        PointF[] ret = new PointF[pts.length];
        for (int i = 0; i < pts.length; i++) {
            ret[i] = parent2Local(pts[i]);
        }
        return ret;
    }

    @Override
    public PointF[] local2Parent(@NonNull RectF rect) {
        PointF[] pts = new PointF[]{new PointF(rect.left, rect.top),
                new PointF(rect.right, rect.top), new PointF(rect.right, rect.bottom),
                new PointF(rect.left, rect.bottom)};

        return local2Parent(pts);
    }

    @Override
    public PointF[] parent2Local(@NonNull RectF rect) {
        PointF[] pts = new PointF[]{new PointF(rect.left, rect.top),
                new PointF(rect.right, rect.top), new PointF(rect.right, rect.bottom),
                new PointF(rect.left, rect.bottom)};
        return parent2Local(pts);
    }

    @Override
    public PointF local2World(@NonNull PointF pt) {
        PointF ret = local2Parent(pt);
        DrawObject item = parent;
        while (item != null) {
            ret = item.local2World(ret);
            item = item.getParent();
        }
        return ret;
    }

    @Override
    public PointF[] local2World(@NonNull PointF[] pt) {
        PointF[] ret = local2Parent(pt);
        DrawObject item = parent;
        while (item != null) {
            ret = item.local2World(ret);
            item = item.getParent();
        }
        return ret;
    }

    @Override
    public PointF[] local2World(@NonNull RectF rect) {
        PointF[] ret = local2Parent(rect);
        DrawObject item = parent;
        while (item != null) {
            ret = item.local2Parent(ret);
            item = item.getParent();
        }
        return ret;
    }

    @Override
    public PointF world2Local(@NonNull PointF pt) {
        DrawObject parent = this.parent;
        PointF ret = pt;
        if (parent != null) {
            ret = parent.world2Local(ret);
        }
        return parent2Local(ret);
    }

    @Override
    public PointF[] world2Local(@NonNull PointF[] pts) {
        PointF[] ret = new PointF[pts.length];
        for (int i = 0; i < pts.length; i++) {
            ret[i] = world2Local(pts[i]);
        }
        return ret;
    }

    @Override
    public PointF[] world2Local(@NonNull RectF rect) {
        PointF[] pts = new PointF[]{new PointF(rect.left, rect.top),
                new PointF(rect.right, rect.top), new PointF(rect.right, rect.bottom),
                new PointF(rect.left, rect.bottom)};
        return world2Local(pts);
    }
    @Override
    public int hitTest(PointF pt) {
        if(!isVisible() && !isSelectable()){
            return 0;
        }
        if(isSelected() && isShowTrack()) {
            Region region = new Region();
            for (int i = 1; i <= getHandleCount(); i++) {
                Path handlePath = getHandleTrack(i);
                region.setPath(handlePath, null);
                if (region.contains((int) pt.x, (int) pt.y))
                    return i;
            }
        }
        if(getRegion().contains((int)pt.x, (int)pt.y)) return getHandleCount() + 1;
        return 0;
    }

    @Override
    public boolean isShowBorder() {
        return showBorder;
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    @Override
    public boolean isFilled() {
        return filled;
    }

    @Override
    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    @Override
    public boolean isShowTrack() {
        return showTrack;
    }

    @Override
    public void setShowTrack(boolean showTrack) {
        this.showTrack = showTrack;
    }
}
