package com.uc.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class ImageMarkOverlay extends View {
    public ImageMarkOverlay(Context context) {
        super(context);
    }

    public ImageMarkOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageMarkOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageMarkOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMarks(canvas);
    }
    protected void drawMarks(Canvas canvas){

    }
}
