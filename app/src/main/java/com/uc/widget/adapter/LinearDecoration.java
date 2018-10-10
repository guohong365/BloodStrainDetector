package com.uc.widget.adapter;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class LinearDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL_LIST=0;
    public static final int VERTICAL_LIST=1;
    private final int width;
    private final Drawable drawable;
    private final int orientation;
    public LinearDecoration(int width, int backgroundColor){
        this(width, new ColorDrawable(backgroundColor));
    }
    public LinearDecoration(int width, int backgroundColor, int orientation){
        this(width, new ColorDrawable(backgroundColor), orientation);
    }
    public LinearDecoration(int width, Drawable background){
        this(width, background, VERTICAL_LIST);
    }
    public LinearDecoration(int width, Drawable background, int orientation){
        this.width=width;
        this.drawable=background;
        this.orientation= (orientation==HORIZONTAL_LIST ? HORIZONTAL_LIST : VERTICAL_LIST) ;
    }
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(orientation== HORIZONTAL_LIST){
            drawHorizontal(c, parent);
        } else {
            drawVertical(c, parent);
        }
    }

    protected void drawVertical(Canvas canvas, RecyclerView parent){
        int left = parent.getPaddingLeft();
        int right= parent.getWidth() - parent.getPaddingRight();
        int childCount=parent.getChildCount();
        for(int i=0; i< childCount; i++){
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params=(RecyclerView.LayoutParams)child.getLayoutParams();
            int top=child.getBottom() + params.bottomMargin;
            int bottom = top + width;
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }
    }
    protected void drawHorizontal(Canvas canvas, RecyclerView parent){
        int top = parent.getPaddingTop();
        int bottom= parent.getHeight() - parent.getPaddingBottom();
        int childCount=parent.getChildCount();
        for(int i=0; i< childCount; i++){
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params=(RecyclerView.LayoutParams)child.getLayoutParams();
            int left=child.getRight() + params.rightMargin;
            int right = left + width;
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(orientation==HORIZONTAL_LIST){
            outRect.set(0,0, width, 0);
        } else {
            outRect.set(0,0, 0, width);
        }
    }
}
