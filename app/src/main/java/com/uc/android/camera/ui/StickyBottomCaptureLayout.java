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
import android.content.res.Configuration;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;


import com.uc.android.camera.CaptureLayoutHelper;
import com.uc.android.camera.ui.motion.InterpolatorHelper;
import com.uc.android.camera.widget.ModeOptions;
import com.uc.android.camera.widget.ModeOptionsOverlay;
import com.uc.android.camera.widget.RoundedThumbnailView;
import com.uc.bloodstraindetector.R;


/**
 * The goal of this class is to ensure mode options and capture indicator is
 * always laid out to the left of or above bottom bar in landscape or portrait
 * respectively. All the other children in this view group can be expected to
 * be laid out the same way as they are in a normal FrameLayout.
 */
public class StickyBottomCaptureLayout extends FrameLayout {

    private final static String TAG = "StickyBotCapLayout";
    private ModeOptionsOverlay mModeOptionsOverlay;
    private View mBottomBar;
    private CaptureLayoutHelper mCaptureLayoutHelper = null;

    public StickyBottomCaptureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        mModeOptionsOverlay = (ModeOptionsOverlay) findViewById(R.id.mode_options_overlay);
        mBottomBar = findViewById(R.id.bottom_bar);
    }

    /**
     * Sets a capture layout helper to query layout rect from.
     */
    public void setCaptureLayoutHelper(CaptureLayoutHelper helper) {
        mCaptureLayoutHelper = helper;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mCaptureLayoutHelper == null) {
            Log.e(TAG, "Capture layout helper needs to be set first.");
            return;
        }
        // Layout mode options overlay.
        RectF uncoveredPreviewRect = mCaptureLayoutHelper.getUncoveredPreviewRect();
        mModeOptionsOverlay.layout((int) uncoveredPreviewRect.left, (int) uncoveredPreviewRect.top,
                (int) uncoveredPreviewRect.right, (int) uncoveredPreviewRect.bottom);

        // Layout bottom bar.
        RectF bottomBarRect = mCaptureLayoutHelper.getBottomBarRect();
        mBottomBar.layout((int) bottomBarRect.left, (int) bottomBarRect.top,
                (int) bottomBarRect.right, (int) bottomBarRect.bottom);
    }
}
