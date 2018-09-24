package com.uc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.uc.R;

public class ProgressWheelView extends View implements StyledAttributesView {
    public interface OnScrollingListener {

        void onScrollStart();

        void onScroll(float delta, float totalDistance, float ticks);

        void onScrollEnd();
    }

    public final int ORIENTATION_VERTICAL = 1;
    public final int ORIENTATION_HORIZONTAL = 0;

    protected int orientation;
    private boolean autoHide;

    private Paint linePaint;
    private int middleLineColor;
    private int lineColor;
    private int lineWidth;
    private int lineLength;
    private int lineSpace;

    private Rect clipBounds = new Rect();

    private OnScrollingListener onScrollingListener;

    private float lastTouchPosition;
    private boolean scrollStarted;
    private float totalScrollDistance;

    public ProgressWheelView(Context context) {
        this(context, null);
    }

    public ProgressWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processStyledAttributes(context, attrs);
    }

    public ProgressWheelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        processStyledAttributes(context, attrs);
    }

    public void setMiddleLineColor(int middleLineColor) {
        this.middleLineColor = middleLineColor;
        invalidate();
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        invalidate();
    }

    public void setTotalScrollDistance(float totalScrollDistance) {
        this.totalScrollDistance = totalScrollDistance;
    }

    public float getTotalScrollDistance() {
        return totalScrollDistance;
    }

    public void setOnScrollingListener(OnScrollingListener onScrollingListener) {
        this.onScrollingListener = onScrollingListener;
    }

    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
        invalidate();
    }

    public int getLineLength() {
        return lineLength;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineSpace(int lineMargin) {
        this.lineSpace = lineMargin;
        invalidate();
    }

    public int getLineSpace() {
        return lineSpace;
    }

    public void processStyledAttributes(Context context, AttributeSet attrs) {
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.ProgressWheelView);
        middleLineColor =array.getColor(R.styleable.ProgressWheelView_middleTickLineColor, context.getResources().getColor(R.color.colorProgressWheelMiddleTickLine, null));
        lineColor=array.getColor(R.styleable.ProgressWheelView_tickLineColor, context.getResources().getColor(R.color.colorProgressWheelTickLine, null));
        lineSpace =array.getDimensionPixelSize(R.styleable.ProgressWheelView_tickLineSpace, getContext().getResources().getDimensionPixelSize(R.dimen.spaceProgressWheelTickLine));
        lineWidth =array.getDimensionPixelSize(R.styleable.ProgressWheelView_tickLineWidth, getContext().getResources().getDimensionPixelSize(R.dimen.widthProgressWheelTickLine));
        lineLength =array.getDimensionPixelSize(R.styleable.ProgressWheelView_tickLineLength, getContext().getResources().getDimensionPixelSize(R.dimen.lengthProgressWheelTickLine));
        totalScrollDistance=array.getFloat(R.styleable.ProgressWheelView_totalValue, 0);
        orientation=array.getInt(R.styleable.ProgressWheelView_orientation, ORIENTATION_HORIZONTAL);
        array.recycle();
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineWidth);
    }

    private void onScrollEvent(MotionEvent event, float distance) {
        totalScrollDistance -= distance;
        postInvalidate();
        lastTouchPosition = orientation == ORIENTATION_HORIZONTAL ? event.getX() : event.getY();
        if (onScrollingListener != null) {
            float ticks = distance / (lineLength + lineSpace);
            onScrollingListener.onScroll(-distance, totalScrollDistance, ticks);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchPosition =orientation==ORIENTATION_HORIZONTAL ? event.getX() : event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (onScrollingListener != null) {
                    scrollStarted=false;
                    onScrollingListener.onScrollEnd();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float distance =(orientation==ORIENTATION_HORIZONTAL ? event.getX():event.getY()) - lastTouchPosition;
                if (distance != 0) {
                    if (!scrollStarted) {
                        scrollStarted = true;
                        if (onScrollingListener != null) {
                            onScrollingListener.onScrollStart();
                        }
                    }
                    onScrollEvent(event, distance);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getClipBounds(clipBounds);

        int linesCount =(orientation==ORIENTATION_HORIZONTAL ? clipBounds.width() : clipBounds.height()) / (lineWidth + lineSpace);
        float delta = (totalScrollDistance) % (float) (lineSpace + lineWidth);

        linePaint.setColor(lineColor);
        for (int i = 0; i < linesCount; i++) {
            if (i < (linesCount / 4)) {
                linePaint.setAlpha((int) (255 * (i / (float) (linesCount / 4))));
            } else if (i > (linesCount * 3 / 4)) {
                linePaint.setAlpha((int) (255 * ((linesCount - i) / (float) (linesCount / 4))));
            } else {
                linePaint.setAlpha(255);
            }
            if(orientation== ORIENTATION_HORIZONTAL) {
                canvas.drawLine(
                    -delta + clipBounds.left + i * (lineWidth + lineSpace),
                    clipBounds.centerY() - lineLength / 4.0f,
                    -delta + clipBounds.left + i * (lineWidth + lineSpace),
                    clipBounds.centerY() + lineLength / 4.0f, linePaint);
            } else {
                canvas.drawLine(
                    clipBounds.centerX() - lineLength / 4.0f,
                    -delta + clipBounds.top + i * (lineWidth + lineSpace),
                    clipBounds.centerX() + lineLength / 4.0f,
                    -delta + clipBounds.top + i * (lineWidth + lineSpace), linePaint);
            }
        }
        linePaint.setColor(middleLineColor);
        if(orientation==ORIENTATION_HORIZONTAL){
            canvas.drawLine(
                clipBounds.centerX(), clipBounds.centerY() - lineLength / 2.0f,
                clipBounds.centerX(), clipBounds.centerY() + lineLength / 2.0f, linePaint);
        } else {
            canvas.drawLine(
                clipBounds.centerX() - lineLength / 2.0f,
                clipBounds.centerY(),
                clipBounds.centerX() + lineLength / 2.0f,
                clipBounds.centerY(), linePaint);
        }
    }
}
