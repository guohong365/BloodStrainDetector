/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uc.android.camera.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.uc.R;


/**
 * GridLines is a view which directly overlays the preview and draws
 * evenly spaced grid lines.
 */
public class GridLines extends View
    implements PreviewStatusListener.PreviewAreaChangedListener {
    private static final String TAG="GridLines";
    //private static final float ratio = 0.787f;
    private RectF mDrawBounds;
    private final Paint mPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
    private final float offset;

    public boolean isGridOn() {
        return gridOn;
    }

    public void setGridOn(boolean gridOn) {
        this.gridOn = gridOn;
        this.invalidate();
    }

    private boolean gridOn = false;

    public GridLines(Context context, AttributeSet attrs) {
        super(context, attrs);
        float width=getResources().getDimensionPixelSize(R.dimen.grid_line_width);
        Log.d(TAG, "GridLines: line width=" + width);
        mPaint.setStrokeWidth(width);
        mPaint.setColor(getResources().getColor(R.color.grid_line, null));
        width=getResources().getDimensionPixelSize(R.dimen.grid_border_width);
        Log.d(TAG, "GridLines: border width=" + width);
        mBorderPaint.setStrokeWidth(width);
        mBorderPaint.setColor(getResources().getColor(R.color.grid_border, null));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        offset = getResources().getDimensionPixelSize(R.dimen.grid_border_width) / 2.0f;
        Log.d(TAG, "GridLines: offset=" + offset);
    }

    private void drawGrid(Canvas canvas, float left, float top, float right, float bottom) {
        float xSpace = (right-left) / 3;
        float ySpace = (bottom - top) / 3;
        float x = xSpace;
        float y = ySpace;
        for (int i = 1; i < 3; i++) {
            // Draw the vertical lines.
            canvas.drawLine(left + x, top, left + x, bottom, mPaint);
            // Draw the horizontal lines.
            canvas.drawLine(left, top + y, right, top + y, mPaint);
            x +=xSpace;
            y +=ySpace;
        }
    }


    private void drawBorder(Canvas canvas,float left, float top, float right, float bottom){
        canvas.drawRect(left , top ,right , bottom , mBorderPaint);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawBounds != null) {
            float minus = offset * 2;
            float height = mDrawBounds.height();
            if(isGridOn()){
                drawGrid(canvas, mDrawBounds.left + minus, mDrawBounds.top + minus, mDrawBounds.right - minus, mDrawBounds.top + height - minus);
            }
            drawBorder(canvas, mDrawBounds.left + offset, mDrawBounds.top + offset, mDrawBounds.right - offset, mDrawBounds.top + height - offset);
        }
    }
    @Override
    public void onPreviewAreaChanged(final RectF previewArea) {
        Log.d(TAG, "onPreviewAreaChanged: RECT=(" + previewArea.left + "," + previewArea.top + "," + previewArea.right + "," + previewArea.bottom + ")");
        setDrawBounds(previewArea);
    }

    private void setDrawBounds(final RectF previewArea) {
        mDrawBounds = new RectF(previewArea);
        invalidate();
    }
}
