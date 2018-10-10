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

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.Log;

import com.uc.android.camera.async.HandlerFactory;
import com.uc.android.camera.async.Lifetime;

import java.util.concurrent.Executor;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Set of device actions for opening and closing a single Camera2 device.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
@ParametersAreNonnullByDefault
public class Camera2Actions implements SingleDeviceActions<CameraDevice> {
    private static final String TAG = "Camera2Act";

    private final CameraDeviceKey mId;
    private final CameraManager mCameraManager;
    private final HandlerFactory mHandlerFactory;
    private final Executor mBackgroundExecutor;

    public Camera2Actions(CameraDeviceKey id,
          CameraManager cameraManager,
          Executor backgroundExecutor,
          HandlerFactory handlerFactory) {
        mId = id;
        mCameraManager = cameraManager;
        mBackgroundExecutor = backgroundExecutor;
        mHandlerFactory = handlerFactory;
        Log.d(TAG,"Created Camera2Request");
    }

    @Override
    public void executeOpen(SingleDeviceOpenListener<CameraDevice> openListener,
          Lifetime deviceLifetime) throws UnsupportedOperationException {
        Log.i(TAG,"executeOpen(id: " + mId.getCameraId() + ")");
        mBackgroundExecutor.execute(new OpenCameraRunnable(mCameraManager,
              mId.getCameraId().getValue(),
              // TODO THIS IS BAD. If there are multiple requests to open,
              // we don't want to add the handler to the lifetime until after
              // the camera device is opened or the camera could be opened with
              // an invalid thread.
              mHandlerFactory.create(deviceLifetime, "Camera2 Lifetime"),
              openListener));
    }

    @Override
    public void executeClose(SingleDeviceCloseListener closeListener, CameraDevice device)
          throws UnsupportedOperationException {
        Log.i(TAG,"executeClose(" + device.getId() + ")");
        mBackgroundExecutor.execute(new CloseCameraRunnable(device, closeListener));
    }

    /**
     * Internal runnable that executes a CameraManager openCamera call.
     */
    private static class OpenCameraRunnable implements Runnable {
        private final SingleDeviceOpenListener<CameraDevice> mOpenListener;
        private final String mCameraId;
        private final Handler mHandler;
        private final CameraManager mCameraManager;

        public OpenCameraRunnable(CameraManager cameraManager, String cameraId,
              Handler handler, SingleDeviceOpenListener<CameraDevice> openListener) {
            mCameraManager = cameraManager;
            mCameraId = cameraId;
            mHandler = handler;
            mOpenListener = openListener;
        }

        @Override
        public void run() {
            try {
                mCameraManager.openCamera(mCameraId, new OpenCameraStateCallback(mOpenListener), mHandler);
            } catch (CameraAccessException | SecurityException | IllegalArgumentException e) {
                mOpenListener.onDeviceOpenException(e);
            }
        }
    }

    /**
     * Internal runnable that executes a close on a cameraDevice.
     */
    private static class CloseCameraRunnable implements Runnable {
        private final SingleDeviceCloseListener mCloseListener;
        private final CameraDevice mCameraDevice;

        public CloseCameraRunnable(CameraDevice cameraDevice,
              SingleDeviceCloseListener closeListener) {
            mCameraDevice = cameraDevice;
            mCloseListener = closeListener;
        }

        @Override
        public void run() {
            try {
                mCameraDevice.close();
                mCloseListener.onDeviceClosed();
            } catch (Exception e) {
                mCloseListener.onDeviceClosingException(e);
            }
        }
    }

    /**
     * Internal callback that provides a camera device to a future.
     */
    private static class OpenCameraStateCallback extends CameraDevice.StateCallback {
        private final SingleDeviceOpenListener<CameraDevice> mOpenListener;
        private boolean mHasBeenCalled = false;

        public OpenCameraStateCallback(SingleDeviceOpenListener<CameraDevice> openListener) {
            mOpenListener = openListener;
        }

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            if (!called()) {
                mOpenListener.onDeviceOpened(cameraDevice);
            }
        }

        @Override
        public void onClosed(CameraDevice cameraDevice) {
            if (!called()) {
                mOpenListener.onDeviceOpenException(cameraDevice);
            }
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            if (!called()) {
                mOpenListener.onDeviceOpenException(cameraDevice);
            }
        }

        @Override
        public void onError(CameraDevice cameraDevice, int errorId) {
            if (!called()) {
                mOpenListener.onDeviceOpenException(new CameraOpenException(errorId));
            }
        }

        private boolean called() {
            boolean result = mHasBeenCalled;
            if (!mHasBeenCalled) {
                mHasBeenCalled = true;
            }

            return result;
        }
    }
}
