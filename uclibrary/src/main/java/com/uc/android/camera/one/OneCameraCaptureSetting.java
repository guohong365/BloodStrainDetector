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

package com.uc.android.camera.one;

import com.uc.android.camera.async.Observable;
import com.uc.android.camera.async.Observables;
import com.uc.android.camera.hardware.HardwareSpec;
import com.uc.android.camera.settings.Keys;
import com.uc.android.camera.settings.SettingObserver;
import com.uc.android.camera.settings.SettingsManager;
import com.uc.android.camera.util.Size;

/**
 * Contains related settings to configure a camera for a particular type of
 * capture.
 */
public class OneCameraCaptureSetting {
    private final Size mCaptureSize;
    private final Observable<OneCamera.PhotoCaptureParameters.Flash> mFlashSetting;
    private final Observable<Integer> mExposureSetting;
    private final Observable<Boolean> mHdrSceneSetting;
    private final boolean mIsHdrPlusEnabled;

    public static OneCameraCaptureSetting create(
            Size pictureSize,
            SettingsManager settingsManager,
            final HardwareSpec hardwareSpec,
            String cameraSettingScope,
            boolean isHdrPlusEnabled) {
        Observable<OneCamera.PhotoCaptureParameters.Flash> flashSetting = new FlashSetting(
                SettingObserver.ofString(settingsManager, cameraSettingScope, Keys.KEY_FLASH_MODE));
        Observable<Integer> exposureSetting = SettingObserver.ofInteger(
                settingsManager, cameraSettingScope, Keys.KEY_EXPOSURE);
        Observable<Boolean> hdrSceneSetting;
        if (hardwareSpec.isHdrSupported()) {
            hdrSceneSetting = SettingObserver.ofBoolean(settingsManager,
                    SettingsManager.SCOPE_GLOBAL, Keys.KEY_CAMERA_HDR);
        } else {
            hdrSceneSetting = Observables.of(false);
        }
        return new OneCameraCaptureSetting(
                pictureSize,
                flashSetting,
                exposureSetting,
                hdrSceneSetting,
                isHdrPlusEnabled);
    }

    private OneCameraCaptureSetting(
            Size captureSize,
            Observable<OneCamera.PhotoCaptureParameters.Flash> flashSetting,
            Observable<Integer> exposureSetting,
            Observable<Boolean> hdrSceneSetting,
            boolean isHdrPlusEnabled) {
        mCaptureSize = captureSize;
        mFlashSetting = flashSetting;
        mExposureSetting = exposureSetting;
        mHdrSceneSetting = hdrSceneSetting;
        mIsHdrPlusEnabled = isHdrPlusEnabled;
    }

    public Size getCaptureSize() {
        return mCaptureSize;
    }

    public Observable<OneCamera.PhotoCaptureParameters.Flash> getFlashSetting() {
        return mFlashSetting;
    }

    public Observable<Integer> getExposureSetting() {
        return mExposureSetting;
    }

    public Observable<Boolean> getHdrSceneSetting() {
        return mHdrSceneSetting;
    }

    public boolean isHdrPlusEnabled() {
        return mIsHdrPlusEnabled;
    }
}
