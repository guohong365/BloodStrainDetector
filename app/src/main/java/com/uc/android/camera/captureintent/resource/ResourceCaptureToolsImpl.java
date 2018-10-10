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

package com.uc.android.camera.captureintent.resource;

import android.media.MediaActionSound;
import android.util.Log;

import com.uc.bloodstraindetector.R;
import com.uc.android.camera.SoundPlayer;
import com.uc.android.camera.async.MainThread;
import com.uc.android.camera.async.RefCountBase;
import com.uc.android.camera.captureintent.CaptureIntentModuleUI;
import com.uc.android.camera.captureintent.CaptureIntentSessionFactory;
import com.uc.android.camera.hardware.HeadingSensor;
import com.uc.android.camera.one.OneCamera;
import com.uc.android.camera.session.CaptureSession;
import com.uc.android.camera.session.CaptureSessionManager;
import com.uc.android.camera.session.CaptureSessionManagerImpl;
import com.uc.android.camera.session.SessionStorageManagerImpl;
import com.uc.android.camera.settings.Keys;
import com.uc.android.camera.settings.SettingsManager;
import com.uc.android.camera.ui.focus.FocusController;
import com.uc.android.camera.ui.focus.FocusSound;
import com.uc.android.camera.util.AndroidServices;
import com.uc.android.camera.util.CameraUtil;
import com.uc.android.util.eventprotos;

public final class ResourceCaptureToolsImpl implements ResourceCaptureTools {
    private static final String TAG = "ResCapTools";

    private final RefCountBase<ResourceConstructed> mResourceConstructed;
    private final RefCountBase<ResourceSurfaceTexture> mResourceSurfaceTexture;
    private final RefCountBase<ResourceOpenedCamera> mResourceOpenedCamera;

    private final CaptureSessionManager mCaptureSessionManager;
    private final FocusController mFocusController;
    private final HeadingSensor mHeadingSensor;
    private final SoundPlayer mSoundPlayer;
    private final MediaActionSound mMediaActionSound;

    /**
     * Creates a reference counted {@link ResourceCaptureToolsImpl} object.
     */
    public static RefCountBase<ResourceCaptureTools> create(
            RefCountBase<ResourceConstructed> resourceConstructed,
            RefCountBase<ResourceSurfaceTexture> resourceSurfaceTexture,
            RefCountBase<ResourceOpenedCamera> resourceOpenedCamera) {
        CaptureSessionManager captureSessionManager = new CaptureSessionManagerImpl(
                new CaptureIntentSessionFactory(),
                SessionStorageManagerImpl.create(resourceConstructed.get().getContext()),
                resourceConstructed.get().getMainThread());
        HeadingSensor headingSensor =
                new HeadingSensor(AndroidServices.instance().provideSensorManager());
        SoundPlayer soundPlayer = new SoundPlayer(resourceConstructed.get().getContext());
        FocusSound focusSound = new FocusSound(soundPlayer, R.raw.material_camera_focus);
        FocusController focusController = new FocusController(
                resourceConstructed.get().getModuleUI().getFocusRing(),
                focusSound,
                resourceConstructed.get().getMainThread());
        MediaActionSound mediaActionSound = new MediaActionSound();
        ResourceCaptureTools resourceCaptureTools = new ResourceCaptureToolsImpl(
                resourceConstructed, resourceSurfaceTexture, resourceOpenedCamera,
                captureSessionManager, focusController, headingSensor, soundPlayer,
                mediaActionSound);
        return new RefCountBase<>(resourceCaptureTools);
    }

    private ResourceCaptureToolsImpl(
            RefCountBase<ResourceConstructed> resourceConstructed,
            RefCountBase<ResourceSurfaceTexture> resourceSurfaceTexture,
            RefCountBase<ResourceOpenedCamera> resourceOpenedCamera,
            CaptureSessionManager captureSessionManager,
            FocusController focusController,
            HeadingSensor headingSensor,
            SoundPlayer soundPlayer,
            MediaActionSound mediaActionSound) {
        mResourceConstructed = resourceConstructed;
        mResourceConstructed.addRef();     // Will be balanced in close().
        mResourceSurfaceTexture = resourceSurfaceTexture;
        mResourceSurfaceTexture.addRef();  // Will be balanced in close().
        mResourceOpenedCamera = resourceOpenedCamera;
        mResourceOpenedCamera.addRef();    // Will be balanced in close().
        mCaptureSessionManager = captureSessionManager;
        mHeadingSensor = headingSensor;
        mHeadingSensor.activate();  // Will be balanced in close().
        mSoundPlayer = soundPlayer;
        mSoundPlayer.loadSound(R.raw.timer_final_second);  // Will be balanced in close().
        mSoundPlayer.loadSound(R.raw.timer_increment);     // Will be balanced in close().
        mMediaActionSound = mediaActionSound;
        mFocusController = focusController;
    }

    @Override
    public void close() {
        Log.d(TAG, "close");
        mResourceConstructed.close();
        mResourceSurfaceTexture.close();
        mResourceOpenedCamera.close();
        mHeadingSensor.deactivate();
        mSoundPlayer.unloadSound(R.raw.timer_increment);
        mSoundPlayer.unloadSound(R.raw.timer_final_second);
    }

    @Override
    public RefCountBase<ResourceConstructed> getResourceConstructed() {
        return mResourceConstructed;
    }

    @Override
    public RefCountBase<ResourceSurfaceTexture> getResourceSurfaceTexture() {
        return mResourceSurfaceTexture;
    }

    @Override
    public RefCountBase<ResourceOpenedCamera> getResourceOpenedCamera() {
        return mResourceOpenedCamera;
    }

    @Override
    public CaptureSessionManager getCaptureSessionManager() {
        return mCaptureSessionManager;
    }

    @Override
    public FocusController getFocusController() {
        return mFocusController;
    }

    @Override
    public MediaActionSound getMediaActionSound() {
        return mMediaActionSound;
    }

    @Override
    public void takePictureNow(
            OneCamera.PictureCallback pictureCallback,
            CaptureLoggingInfo captureLoggingInfo) {
        final ResourceConstructed resource = mResourceConstructed.get();
        final ResourceOpenedCamera openedCamera = mResourceOpenedCamera.get();

        /**
         * Disable the shutter button immediately. The button will be
         * re-enabled when users press re-take button.
         */
        resource.getMainThread().execute(new Runnable() {
            @Override
            public void run() {
                resource.getModuleUI().setShutterButtonEnabled(false);
            }
        });

        /** Create a new capture session. */
        final long timestamp = System.currentTimeMillis();
        final String fileName = CameraUtil.instance().createJpegName(timestamp);
        final android.location.Location location =
                resource.getLocationManager().getCurrentLocation();
        final CaptureSession session =
                mCaptureSessionManager.createNewSession(fileName, timestamp, location);
        session.startEmpty(null, openedCamera.getPictureSize());

        /** Logging */
        final SettingsManager settingsManager = resource.getSettingsManager();
        boolean isGridLinesOn = Keys.areGridLinesOn(settingsManager);

        OneCamera.PhotoCaptureParameters params = new OneCamera.PhotoCaptureParameters(
                session.getTitle(),
                resource.getOrientationManager().getDeviceOrientation().getDegrees(),
                session.getLocation(),
                resource.getContext().getExternalCacheDir(),
                pictureCallback,
                mPictureSaverCallback,
                mHeadingSensor.getCurrentHeading(),
                openedCamera.getZoomRatio(),
                0);
        openedCamera.getCamera().takePicture(params, session);
    }

    @Override
    public void playCountDownSound(int remainingSeconds) {
        if (remainingSeconds == 1) {
            mSoundPlayer.play(R.raw.timer_final_second, 0.6f);
        } else if (remainingSeconds == 2 || remainingSeconds == 3) {
            mSoundPlayer.play(R.raw.timer_increment, 0.6f);
        }
    }

    @Override
    public MainThread getMainThread() {
        return mResourceConstructed.get().getMainThread();
    }

    @Override
    public CaptureIntentModuleUI getModuleUI() {
        return mResourceConstructed.get().getModuleUI();
    }

    @Override
    public OneCamera getCamera() {
        return mResourceOpenedCamera.get().getCamera();
    }

    private final OneCamera.PictureSaverCallback mPictureSaverCallback =
            new OneCamera.PictureSaverCallback() {
                @Override
                public void onRemoteThumbnailAvailable(byte[] jpegImage) {
                }
            };
}
