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

import com.uc.android.camera.FatalErrorHandler;
import com.uc.android.util.eventprotos;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Handles repeat failure by displaying the fatal error dialog (which also
 * finishes the activity).
 */
@ParametersAreNonnullByDefault
final class FatalErrorDialogFailureHandler implements FailureHandler {
    private final FatalErrorHandler mFatalErrorHandler;

    FatalErrorDialogFailureHandler(FatalErrorHandler fatalErrorHandler) {
        mFatalErrorHandler = fatalErrorHandler;
    }

    @Override
    public void run() {

        // TODO Add another {@link FatalErrorHandler.Reason} for this situation
        mFatalErrorHandler.handleFatalError(FatalErrorHandler.Reason.CANNOT_CONNECT_TO_CAMERA);
    }
}
