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

package com.uc.android.camera.device;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

import com.uc.android.camera.async.HandlerFactory;
import com.uc.android.camera.async.Lifetime;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Set of device actions for opening and closing a single Legacy camera
 * device.
 */
@ParametersAreNonnullByDefault
public class LegacyCameraActions implements SingleDeviceActions<Camera> {
    private static final String TAG= "Camera1Act";

    private final CameraDeviceKey mId;
    private final HandlerFactory mHandlerFactory;

    @Nullable
    private Handler mCameraHandler;

    public LegacyCameraActions(CameraDeviceKey id, HandlerFactory handlerFactory) {
        mId = id;
        mHandlerFactory = handlerFactory;
    }

    @Override
    public void executeOpen(SingleDeviceOpenListener<Camera> openListener,
          Lifetime deviceLifetime) throws UnsupportedOperationException {
        Log.i(TAG,"executeOpen(id: " + mId.getCameraId() + ")");

        mCameraHandler = mHandlerFactory.create(deviceLifetime, "LegacyCamera Handler");
        mCameraHandler.post(new OpenCameraRunnable(openListener,
              mId.getCameraId().getLegacyValue()));
    }

    @Override
    public void executeClose(SingleDeviceCloseListener closeListener, Camera device)
          throws UnsupportedOperationException {
        Log.i(TAG,"executeClose(" + mId.getCameraId() + ")");

        Runnable closeCamera = new CloseCameraRunnable(closeListener,
              device,
              mId.getCameraId().getLegacyValue());

        if (mCameraHandler != null) {
            mCameraHandler.post(closeCamera);
        } else {
            Log.e(TAG,"executeClose() was executed before the handler was created!");
            closeCamera.run();
        }
    }

    /**
     * Internal runnable that calls Camera.open and creates a new
     * camera device.
     */
    private static class OpenCameraRunnable implements Runnable {
        private final SingleDeviceOpenListener<Camera> mResults;
        private final int mCameraId;
        public OpenCameraRunnable(SingleDeviceOpenListener<Camera> results,
              int cameraId) {
            mCameraId = cameraId;
            mResults = results;
        }

        @Override
        public void run() {
            try {
                Camera device = Camera.open(mCameraId);
                mResults.onDeviceOpened(device);
            } catch (RuntimeException e) {
                mResults.onDeviceOpenException(e);
            }
        }
    }

    /**
     * Internal runnable that releases the Camera device.
     */
    private static class CloseCameraRunnable implements Runnable {
        private final SingleDeviceCloseListener mCloseListener;
        private final int mCameraId;
        private final Camera mCameraDevice;
        public CloseCameraRunnable(SingleDeviceCloseListener closeListener,
              Camera cameraDevice,
              int cameraId) {
            mCameraDevice = cameraDevice;
            mCloseListener = closeListener;
            mCameraId = cameraId;
        }

        @Override
        public void run() {
            try {
                mCameraDevice.release();
                mCloseListener.onDeviceClosed();
            } catch (Exception e) {
                mCloseListener.onDeviceClosingException(e);
            }
        }
    }
}
