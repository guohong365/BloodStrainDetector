package com.uc.widget.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class GridDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG="GridDecoration";
    private final int dividerSize;
    private final Drawable backgroundDrawable;
    public GridDecoration(int dividerSize, int background){
        this.dividerSize=dividerSize;
        this.backgroundDrawable=new ColorDrawable(background);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    protected void drawHorizontal(Canvas canvas, RecyclerView parent){
        final int childCount = parent.getChildCount();
        for(int i=0; i< childCount; i++){
            final View child=parent.getChildAt(i);
            final RecyclerView.LayoutParams params= (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left=child.getLeft() - params.leftMargin;
            final int right=child.getRight() + params.rightMargin + dividerSize;
            final int top=child.getBottom() + params.bottomMargin;
            final int bottom= top + dividerSize;
            backgroundDrawable.setBounds(left, top, right, bottom);
            backgroundDrawable.draw(canvas);
        }
    }
    protected void drawVertical(Canvas canvas, RecyclerView parent){
        final int childCount=parent.getChildCount();
        for(int i=0; i< childCount; i++){
            final View child=parent.getChildAt(i);
            final RecyclerView.LayoutParams params=(RecyclerView.LayoutParams)child.getLayoutParams();
            final int top = child.getTop() + params.topMargin;
            final int bottom= child.getBottom() + params.bottomMargin;
            final int left= child.getRight() + params.rightMargin;
            final int right = left + dividerSize;
            backgroundDrawable.setBounds(left, top, right, bottom);
            backgroundDrawable.draw(canvas);
        }
    }
    protected int getSpanCount(RecyclerView parent){
        int spanCount=-1;
        RecyclerView.LayoutManager layoutManager=parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager) {
            spanCount=((GridLayoutManager)layoutManager).getSpanCount();
        } else if(layoutManager instanceof StaggeredGridLayoutManager){
            spanCount=((StaggeredGridLayoutManager)layoutManager).getSpanCount();
        }
        return spanCount;
    }

    protected boolean isLastColumn(RecyclerView parent, int position, int spanCount, int childCount){
        RecyclerView.LayoutManager layoutManager=parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            return (position + 1) % spanCount == 0;
        } else if(layoutManager instanceof StaggeredGridLayoutManager){
            int orientation = ((StaggeredGridLayoutManager)layoutManager).getOrientation();
            if(orientation==StaggeredGridLayoutManager.VERTICAL){
                return (position + 1) % spanCount == 0;
            } else {
                return position >= (childCount - childCount % spanCount);
            }
        }
        return false;
    }
    protected boolean isLastRow(RecyclerView parent, int position, int spanCount, int childCount){
        RecyclerView.LayoutManager layoutManager=parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            return position >= (childCount - childCount % spanCount);
        } else if(layoutManager instanceof StaggeredGridLayoutManager){
            int orientation = ((StaggeredGridLayoutManager)layoutManager).getOrientation();
            if(orientation==StaggeredGridLayoutManager.VERTICAL){
                return position >= (childCount - childCount % spanCount);
            } else {
                return (position + 1) % spanCount == 0;
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount=getSpanCount(parent);
        int childCount=parent.getAdapter().getItemCount();
        int position = parent.getChildAdapterPosition(view);
        if(isLastRow(parent, position, spanCount, childCount)){
            outRect.set(0,0, dividerSize, 0);
        } else if(isLastColumn(parent, position, spanCount, childCount)){
            outRect.set(0,0,0, dividerSize);
        } else {
            outRect.set(0,0, dividerSize, dividerSize);
        }

    }
}
