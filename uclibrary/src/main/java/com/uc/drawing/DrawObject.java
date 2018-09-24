package com.uc.drawing;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.util.SizeF;
import android.view.MotionEvent;

import com.uc.model.Selectable;
import com.uc.model.Selectable.SelectedObserved;

public interface DrawObject extends Selectable, Movable, Scalable, Rotatable, SelectedObserved{
    PointF LOCAL_ORIGINAL=new PointF(0,0);
    int TOUCH_TOLERANT_RADIUS= 10;
    int PROPERTY_POSITION = 1;
    int PROPERTY_SIZE = 2;

    //几何属性
    PointF getPosition();
    void setPosition(@NonNull PointF position);
    void setPosition(float x, float y);
    SizeF getSize();
    PointF getRotateCenter();
    void setRotateCenter(@NonNull PointF rotateCenter);
    RectF getBounds();
    Region getRegion();
    PointF getCenter();
    Path getClip();
    int getHandleCount();
    PointF getHandle(int handle);
    //状态
    boolean isVisible();
    void setVisible(boolean visible);
    boolean isActive(); ///显示为激活
    void setActive(boolean active);
    boolean isShowTrack();
    void setShowTrack(boolean showTrack);
    boolean isShowBorder();
    void setShowBorder(boolean showBorder);
    boolean isFilled();
    void setFilled(boolean filled);

    //行为
    boolean isSizable();
    void setSizable(boolean sizable);
    //结构
    DrawObject getParent();
    void setParent(DrawObject parent);

    Appearance getBackgroundAppearance();
    void setBackgroundAppearance(@NonNull Appearance appearance);
    Appearance getBorderAppearance();
    void setBorderAppearance(@NonNull Appearance appearance);
    Appearance getTrackAppearance();
    void setTrackAppearance(@NonNull Appearance appearance);
    Appearance getAppearance();
    void setAppearance(@NonNull Appearance appearance);

    void draw(Canvas canvas);

    void addOnPropertyChangingListener(int propertyId,@NonNull OnPropertyChangingListener listener);

    void addOnPropertyChangedListener(int propertyId, @NonNull OnPropertyChangedListener listener);

    PointF local2Parent(@NonNull PointF pt);

    PointF[] local2Parent(@NonNull PointF[] pt);

    PointF parent2Local(@NonNull PointF pt);

    PointF[] parent2Local(@NonNull PointF[] pts);

    //helper

    PointF[] local2Parent(@NonNull RectF rect);

    PointF[] parent2Local(@NonNull RectF rect);

    PointF local2World(@NonNull PointF pt);

    PointF[] local2World(@NonNull PointF[] pt);

    PointF world2Local(@NonNull PointF pt);

    PointF[] world2Local(@NonNull PointF[] pts);

    PointF[] local2World(@NonNull RectF rect);

    PointF[] world2Local(@NonNull RectF rect);

    //action
    int hitTest(PointF pt);

    int moveHandleTo(int handle, PointF pt);

    interface OnPropertyChangingListener{
        boolean onPropertyChanging(@NonNull DrawObject sender, int propertyId, Object value);
    }
    interface OnPropertyChangedListener{
        void onPropertyChanged(@NonNull DrawObject sender, int propertyId);
    }
    interface PropertyChangingNotifier{
        boolean notify(int propertyId, Object value);
    }


    interface PropertyChangedNotifier{
        boolean notify(int propertyId);
    }

    interface OnTouchListener{
        boolean onTouch(PointF pt, MotionEvent event);
    }
}

