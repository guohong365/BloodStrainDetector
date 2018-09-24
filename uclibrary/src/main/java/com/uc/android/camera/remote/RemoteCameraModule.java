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

package com.uc.android.camera.remote;

/**
 * Modules implementing this interface signal that they do support remote
 * shutter. Such modules need to signal to the remote interface that they are
 * available or unavailable through calling
 * {@link RemoteShutterListener#onModuleReady(RemoteCameraModule)} and
 * {@link RemoteShutterListener#onModuleExit()}.
 */
public interface RemoteCameraModule {
    /**
     * Called when a remote client wants the module to take a picture. The
     * module should trigger a capture and send the result via
     * {@link RemoteShutterListener#onPictureTaken(byte[])}.
     */
    void onRemoteShutterPress();
}
