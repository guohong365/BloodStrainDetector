/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.uc.android.camera.one.v2.errorhandling;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build.VERSION_CODES;
import android.util.Log;


import com.uc.android.camera.one.v2.core.ResponseListener;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Detect jank in the preview by detecting large percentage increases in the time
 * delta between the sensor timestamps retrieved from the camera.
 */
@ParametersAreNonnullByDefault
@TargetApi(VERSION_CODES.LOLLIPOP)
public final class FramerateJankDetector extends ResponseListener {
    private static final double FRACTIONAL_CHANGE_STATS_THRESHOLD = .5;
    private static final double FRACTIONAL_CHANGE_LOG_THRESHOLD = 1.5;

    private final String TAG="FrameJank";

    private long mLastFrameTimestamp = -1;
    private double mLastDeltaMillis = 0.0;

    @Override
    public void onCompleted(TotalCaptureResult result) {
        long timestamp = result.get(CaptureResult.SENSOR_TIMESTAMP);
        if (mLastFrameTimestamp >= 0) {
            double deltaMillis = (timestamp - mLastFrameTimestamp) / 1000000.0;

            if (mLastDeltaMillis > 0) {
                double fractionalChange = (deltaMillis - mLastDeltaMillis) / mLastDeltaMillis;

                if (fractionalChange >= FRACTIONAL_CHANGE_LOG_THRESHOLD) {
                    Log.v(TAG,"JANK! Time between frames (" + deltaMillis + "ms) increased by " +
                          (fractionalChange * 100) + "% over the last frame delta (" +
                          mLastDeltaMillis + "ms)");
                }
            }
            mLastDeltaMillis = deltaMillis;
        }

        mLastFrameTimestamp = timestamp;
    }
}
