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
import android.hardware.Camera;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import com.uc.android.camera.async.Lifetime;
import com.uc.android.camera.device.CameraDeviceKey.ApiType;
import com.uc.android.ex.camera2.portability.CameraAgent.CameraProxy;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Future;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.GuardedBy;

/**
 * This class's only job is to open and close camera devices safely, such that
 * only one device of any type is open at any point in time. This provider
 * operates on the principle that the most recent request wins.
 *
 * The logic for opening a camera device proceeds as follows:
 *
 * 1. If there is no open camera, create a new camera request, and open
 *    the device.
 * 2. If there is an existing request, and the device id's match,
 *    then reuse the old request and cancel any outstanding "please
 *    open this device next" requests. However, if the previous future
 *    for that current device was not yet completed, cancel it, and
 *    create a new future that will then get returned.
 * 3. If there is an existing request, but the device ids don't match,
 *    cancel any outstanding "please open this device next" requests.
 *    Then create a new request and return the future and begin the shutdown
 *    process on the current device holder. However, do NOT begin opening
 *    this device until the current device is closed.
 */
@ParametersAreNonnullByDefault
public class MultiCameraDeviceLifecycle {
    private static final String TAG = "MltiDeviceLife";

    private static class Singleton {
        private static final MultiCameraDeviceLifecycle INSTANCE = new MultiCameraDeviceLifecycle(
              CameraModuleHelper.provideLegacyCameraActionProvider(),
              CameraModuleHelper.providePortabilityActionProvider(),
              CameraModuleHelper.provideCamera2ActionProvider(),
              ActiveCameraDeviceTracker.instance());
    }

    public static MultiCameraDeviceLifecycle instance() {
        return Singleton.INSTANCE;
    }

    private final LegacyCameraActionProvider mLegacyCameraActionProvider;
    private final PortabilityCameraActionProvider mPortabilityCameraActionProvider;
    private final Camera2ActionProvider mCamera2ActionProvider;
    private final ActiveCameraDeviceTracker mActiveCameraDeviceTracker;

    private final Object mDeviceLock = new Object();

    @Nullable
    @GuardedBy("mDeviceLock")
    private SingleDeviceLifecycle mCurrentDevice;

    @Nullable
    @GuardedBy("mDeviceLock")
    private SingleDeviceLifecycle mTargetDevice;

    @Nullable
    @GuardedBy("mDeviceLock")
    private SettableFuture<Void> mShutdownFuture;

    @VisibleForTesting
    MultiCameraDeviceLifecycle(
          LegacyCameraActionProvider legacyCameraActionProvider,
          PortabilityCameraActionProvider portabilityCameraActionProvider,
          Camera2ActionProvider camera2ActionProvider,
          ActiveCameraDeviceTracker activeCameraDeviceTracker) {
        mLegacyCameraActionProvider = legacyCameraActionProvider;
        mPortabilityCameraActionProvider = portabilityCameraActionProvider;
        mCamera2ActionProvider = camera2ActionProvider;
        mActiveCameraDeviceTracker = activeCameraDeviceTracker;

        Log.d(TAG,"Creating the CameraDeviceProvider.");
    }

    /**
     * !!! Warning !!!
     * Code using this class should close the camera device by closing the
     * provided lifetime instead of calling close directly on the camera
     * object. Failing to do so may leave the multi camera lifecycle in an
     * inconsistent state.
     *
     * Returns a future to an API2 Camera device. The future will only
     * complete if the camera is fully opened successfully. If the device cannot
     * be opened, the future will be canceled or provided with an exception
     * depending on the nature of the internal failure. This call will not block
     * the calling thread.
     *
     * @param requestLifetime the lifetime for the duration of the request.
     *     Closing the lifetime will cancel any outstanding request and will
     *     cause the camera to close. Closing the lifetime instead of the device
     *     will ensure everything is shut down properly.
     * @param cameraId the specific camera device to open.
     */
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public ListenableFuture<android.hardware.camera2.CameraDevice> openCamera2Device(
            Lifetime requestLifetime, CameraId cameraId) {
        CameraDeviceKey key = new CameraDeviceKey(ApiType.CAMERA_API2, cameraId);
        return openDevice(requestLifetime, key, mCamera2ActionProvider);
    }

    /**
     * !!! Warning !!!
     * Code using this class should close the camera device by closing the
     * provided lifetime instead of calling close directly on the camera
     * object. Failing to do so may leave the multi camera lifecycle in an
     * inconsistent state.
     *
     * This returns a future to a CameraProxy device in auto mode and does not
     * make any guarantees about the backing API version. The future will only
     * return if the device is fully opened successfully. If the device cannot
     * be opened, the future will be canceled or provided with an exception
     * depending on the nature of the internal failure. This call will not block
     * the calling thread.
     *
     * @param requestLifetime the lifetime for the duration of the request.
     *     Closing the lifetime will cancel any outstanding request and will
     *     cause the camera to close. Closing the lifetime instead of the device
     *     will ensure everything is shut down properly.
     * @param cameraId the specific camera device to open.
     */
    public ListenableFuture<CameraProxy> openPortabilityDevice(
            Lifetime requestLifetime, CameraId cameraId) {
        CameraDeviceKey key = new CameraDeviceKey(ApiType.CAMERA_API_PORTABILITY_AUTO,
              cameraId);
        return openDevice(requestLifetime, key, mPortabilityCameraActionProvider);
    }

    /**
     * !!! Warning !!!
     * Code using this class should close the camera device by closing the
     * provided lifetime instead of calling close directly on the camera
     * object. Failing to do so may leave the multi camera lifecycle in an
     * inconsistent state.
     *
     * This returns a future to a CameraProxy device opened explicitly with an
     * API2 backing device. The future will only return if the device is fully
     * opened successfully. If the device cannot be opened, the future will be
     * canceled or provided with an exception depending on the nature of the
     * internal failure. This call will not block the calling thread.
     *
     * @param requestLifetime the lifetime for the duration of the request.
     *     Closing the lifetime will cancel any outstanding request and will
     *     cause the camera to close. Closing the lifetime instead of the device
     *     will ensure everything is shut down properly.
     * @param cameraId the specific camera device to open.
     */
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public ListenableFuture<CameraProxy> openCamera2PortabilityDevice(
            Lifetime requestLifetime, CameraId cameraId) {
        CameraDeviceKey key = new CameraDeviceKey(ApiType.CAMERA_API_PORTABILITY_API2,
              cameraId);
        return openDevice(requestLifetime, key, mPortabilityCameraActionProvider);
    }

    /**
     * !!! Warning !!!
     * Code using this class should close the camera device by closing the
     * provided lifetime instead of calling close directly on the camera
     * object. Failing to do so may leave the multi camera lifecycle in an
     * inconsistent state.
     *
     * This returns a future to a CameraProxy device opened explicitly with an
     * legacy backing API. The future will only return if the device is fully
     * opened successfully. If the device cannot be opened, the future will be
     * canceled or provided with an exception depending on the nature of the
     * internal failure. This call will not block the calling thread.
     *
     * @param requestLifetime the lifetime for the duration of the request.
     *     Closing the lifetime will cancel any outstanding request and will
     *     cause the camera to close. Closing the lifetime instead of the device
     *     will ensure everything is shut down properly.
     * @param cameraId the specific camera device to open.
     */
    public ListenableFuture<CameraProxy> openLegacyPortabilityDevice(
            Lifetime requestLifetime, CameraId cameraId) {
        CameraDeviceKey key = new CameraDeviceKey(ApiType.CAMERA_API_PORTABILITY_API1, cameraId);
        return openDevice(requestLifetime, key, mPortabilityCameraActionProvider);
    }
    /**
     * !!! Warning !!!
     * Code using this class should close the camera device by closing the
     * provided lifetime instead of calling close directly on the camera
     * object. Failing to do so may leave the multi camera lifecycle in an
     * inconsistent state.
     *
     * This returns a future to a legacy Camera device The future will only return
     * if the device is fully opened successfully. If the device cannot be opened,
     * the future will be canceled or provided with an exception depending on the
     * nature of the internal failure. This call will not block the calling thread.
     *
     * @param requestLifetime the lifetime for the duration of the request.
     *     Closing the lifetime will cancel any outstanding request and will
     *     cause the camera to close. Closing the lifetime instead of the device
     *     will ensure everything is shut down properly.
     * @param cameraId the specific camera device to open.
     */
    @Deprecated
    public ListenableFuture<Camera> openLegacyCameraDevice(Lifetime requestLifetime,
          CameraId cameraId) {
        CameraDeviceKey key = new CameraDeviceKey(ApiType.CAMERA_API1, cameraId);
        return openDevice(requestLifetime, key, mLegacyCameraActionProvider);
    }

    /**
     * This will close any open or pending requests and will execute the future
     * when all requests have been cleared out. This method executes immediately.
     */
    public ListenableFuture<Void> shutdown() {
        synchronized (mDeviceLock) {
            Log.d(TAG,"shutdownAsync()");
            if (mCurrentDevice != null) {
                // Ensure there are no queued requests. Cancel any existing requests.
                clearTargetDevice();

                // Create a future that we can complete when the device completes
                // its shutdown cycle.
                mShutdownFuture = SettableFuture.create();

                // Execute close on the current device.
                mCurrentDevice.close();
                return mShutdownFuture;
            } else if (mShutdownFuture != null) {
                // This could occur if a previous shutdown call occurred, and
                // the receiver called cancel on the future before it completed.
                if (mShutdownFuture.isDone()) {
                    mShutdownFuture = null;
                } else {
                    return mShutdownFuture;
                }
            }
            // If there is no currently open device, this instance is already in a
            // clean shutdown state.
            return Futures.immediateFuture(null);
        }
    }

    /**
     * Given a request lifetime, a key and a provider, open a new device.
     */
    private <TDevice> ListenableFuture<TDevice> openDevice(Lifetime requestLifetime,
          CameraDeviceKey key, CameraDeviceActionProvider<TDevice> provider) {

        final SingleDeviceLifecycle<TDevice, CameraDeviceKey> deviceLifecycle;
        final ListenableFuture<TDevice> result;

        synchronized (mDeviceLock) {
            Log.d(TAG,"[openDevice()] open(cameraId: '" + key + "')");
            cancelShutdown();

            if (mCurrentDevice == null) {
                Log.d(TAG,"[openDevice()] No existing request. Creating a new device.");
                deviceLifecycle = createLifecycle(key, provider);
                mCurrentDevice = deviceLifecycle;
                result = deviceLifecycle.createRequest(requestLifetime);
                deviceLifecycle.open();
                mActiveCameraDeviceTracker.onCameraOpening(key.getCameraId());
            } else if (mCurrentDevice.getId().equals(key)) {
                Log.d(TAG,"[openDevice()] Existing request with the same id.");
                deviceLifecycle =
                      (SingleDeviceLifecycle<TDevice, CameraDeviceKey>) mCurrentDevice;
                clearTargetDevice();
                result = deviceLifecycle.createRequest(requestLifetime);
                deviceLifecycle.open();
                mActiveCameraDeviceTracker.onCameraOpening(key.getCameraId());
            } else {
                Log.d(TAG,"[openDevice()] Existing request with a different id.");
                mCurrentDevice.close();
                deviceLifecycle = createLifecycle(key, provider);
                clearTargetDevice();
                mTargetDevice = deviceLifecycle;
                result = deviceLifecycle.createRequest(requestLifetime);
            }

            Log.d(TAG,"[openDevice()] Returning future.");
            return result;
        }
    }

    private <TDevice> SingleDeviceLifecycle<TDevice, CameraDeviceKey>
        createLifecycle(CameraDeviceKey key,
          CameraDeviceActionProvider<TDevice> provider) {
        SingleDeviceShutdownListener<CameraDeviceKey> listener =
              new SingleDeviceShutdownListener<CameraDeviceKey>() {
                  @Override
                  public void onShutdown(CameraDeviceKey key) {
                      onCameraDeviceShutdown(key);
                  }
              };

        SingleDeviceStateMachine<TDevice, CameraDeviceKey> deviceState =
              new SingleDeviceStateMachine<>(provider.get(key), key, listener);

        return new CameraDeviceLifecycle<>(key, deviceState);
    }

    private void clearTargetDevice() {
        if (mTargetDevice != null) {
            Log.d(TAG,"Target request exists. cancel() and clear.");
            mTargetDevice.close();
            mTargetDevice = null;
        }
    }

    private void cancelShutdown() {
        if (mShutdownFuture != null) {
            Log.i(TAG,"Canceling shutdown.");
            Future<Void> shutdownFuture = mShutdownFuture;
            mShutdownFuture = null;
            shutdownFuture.cancel(true /* mayInterruptIfRunning */);
        }
    }

    private void completeShutdown() {
        if (mShutdownFuture != null) {
            Log.i(TAG,"Completing shutdown.");
            SettableFuture<Void> shutdownFuture = mShutdownFuture;
            mShutdownFuture = null;
            shutdownFuture.set(null);
        }
    }

    private void onCameraDeviceShutdown(CameraDeviceKey key) {
        synchronized (mDeviceLock) {
            Log.d(TAG,"onCameraClosed(id: " + key + ").");
            if (mShutdownFuture != null &&
                  (mCurrentDevice == null || (mCurrentDevice.getId().equals(key)))) {
                // if the shutdown future is set but there is no current device,
                // we should call shutdown, just in case so that it clears out the
                // shutdown state. If
                mCurrentDevice = null;
                completeShutdown();
            } if (mCurrentDevice != null && mCurrentDevice.getId().equals(key)) {

                Log.d(TAG,"Current device was closed.");

                if (mTargetDevice != null) {
                    Log.d(TAG,"Target request exists, calling open().");
                    mCurrentDevice = mTargetDevice;
                    mTargetDevice = null;
                    mCurrentDevice.open();
                    mActiveCameraDeviceTracker.onCameraOpening(((CameraDeviceKey)
                          mCurrentDevice.getId()).getCameraId());
                } else {
                    Log.d(TAG,"No target request exists. Clearing current device.");
                    mCurrentDevice = null;
                    mActiveCameraDeviceTracker.onCameraClosed(key.getCameraId());
                }
            }
        }
    }
}
