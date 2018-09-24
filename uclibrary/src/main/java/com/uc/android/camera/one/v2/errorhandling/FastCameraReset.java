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

import android.hardware.camera2.CameraAccessException;
import android.util.Log;

import com.uc.android.camera.one.v2.camera2proxy.CameraCaptureSessionClosedException;
import com.uc.android.camera.one.v2.camera2proxy.CameraCaptureSessionProxy;
import com.uc.android.camera.one.v2.commands.CameraCommandExecutor;
import com.uc.android.util.eventprotos;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Resets camera usage by calling abortCaptures(), flushing (interrupting) any
 * currently-executing camera commands, and restarting the preview.
 * <p>
 * Workaround for Bug: 19061883
 */
@ParametersAreNonnullByDefault
final class FastCameraReset implements FailureHandler {
    private static final String TAG="FastCameraReset";
    private final CameraCaptureSessionProxy mCaptureSession;
    private final CameraCommandExecutor mCommandExecutor;
    private final Runnable mPreviewStarter;

    FastCameraReset(CameraCaptureSessionProxy captureSession,
                    CameraCommandExecutor commandExecutor, Runnable previewStarter) {
        mCaptureSession = captureSession;
        mCommandExecutor = commandExecutor;
        mPreviewStarter = previewStarter;
    }

    @Override
    public void run() {


        Log.w(TAG,"beginning reset()");
        try {
            Log.w(TAG,"abortCaptures()");
            mCaptureSession.abortCaptures();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (CameraCaptureSessionClosedException e) {
            e.printStackTrace();
        }
        Log.w(TAG,"flushing existing camera commands");
        mCommandExecutor.flush();
        Log.w(TAG,"restarting the preview");
        mPreviewStarter.run();
        Log.w(TAG,"finished reset()");
    }
}
