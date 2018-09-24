/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.uc.android.camera.module;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.uc.R;
import com.uc.android.camera.CaptureModule;
import com.uc.android.camera.PhotoModule;
import com.uc.android.camera.app.AppController;
import com.uc.android.camera.app.ModuleManager;
import com.uc.android.camera.captureintent.CaptureIntentModule;
import com.uc.android.camera.one.OneCamera;
import com.uc.android.camera.one.OneCameraException;
import com.uc.android.camera.one.config.OneCameraFeatureConfig;
import com.uc.android.camera.one.config.OneCameraFeatureConfig.HdrPlusSupportLevel;
import com.uc.android.camera.settings.SettingsScopeNamespaces;
import com.uc.android.camera.util.PhotoSphereHelper;
import com.uc.android.camera.util.RefocusHelper;


/**
 * A class holding the module information and registers them to
 * {@link com.uc.android.camera.app.ModuleManager}.
 */
public class ModulesInfo {
    private static final String TAG = "ModulesInfo";

    public static void setupModules(Context context, ModuleManager moduleManager,
            OneCameraFeatureConfig config) {
        Resources res = context.getResources();
        int photoModuleId = context.getResources().getInteger(R.integer.camera_mode_photo);
        
        registerPhotoModule(moduleManager, photoModuleId, SettingsScopeNamespaces.PHOTO,
                config.isUsingCaptureModule());
        Log.d(TAG, "setupModules: photo module registed.");
        moduleManager.setDefaultModuleIndex(photoModuleId);
        Log.d(TAG, "setupModules: set photo module as default.");
        if (RefocusHelper.hasRefocusCapture(context)) {
            Log.d(TAG, "setupModules: has refocus capture.");
            registerRefocusModule(moduleManager, res.getInteger(R.integer.camera_mode_refocus),
                    SettingsScopeNamespaces.REFOCUS);
            Log.d(TAG, "setupModules: refocus module registed.");
        }
        int imageCaptureIntentModuleId = res.getInteger(R.integer.camera_mode_capture_intent);
        registerCaptureIntentModule(moduleManager, imageCaptureIntentModuleId,
                SettingsScopeNamespaces.PHOTO, config.isUsingCaptureModule());
        Log.d(TAG, "setupModules: cature module registed.");
    }

    private static void registerPhotoModule(ModuleManager moduleManager, final int moduleId,
            final String namespace, final boolean enableCaptureModule) {
        moduleManager.registerModule(new ModuleManager.ModuleAgent() {

            @Override
            public int getModuleId() {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera() {
                // The PhotoModule requests the old app camere, while the new
                // capture module is using OneCamera. At some point we'll
                // refactor all modules to use OneCamera, then the new module
                // doesn't have to manage it itself.
                return !enableCaptureModule;
            }

            @Override
            public String getScopeNamespace() {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent) {
                Log.v(TAG, "EnableCaptureModule = " + enableCaptureModule);
                return enableCaptureModule ? new CaptureModule(app) : new PhotoModule(app);
            }
        });
    }

    private static void registerWideAngleModule(ModuleManager moduleManager, final int moduleId,
            final String namespace) {
        moduleManager.registerModule(new ModuleManager.ModuleAgent() {
            @Override
            public int getModuleId() {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera() {
                return true;
            }

            @Override
            public String getScopeNamespace() {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent) {
                return PhotoSphereHelper.createWideAnglePanoramaModule(app);
            }
        });
    }

    private static void registerPhotoSphereModule(ModuleManager moduleManager, final int moduleId,
            final String namespace) {
        moduleManager.registerModule(new ModuleManager.ModuleAgent() {
            @Override
            public int getModuleId() {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera() {
                return true;
            }

            @Override
            public String getScopeNamespace() {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent) {
                return PhotoSphereHelper.createPanoramaModule(app);
            }
        });
    }

    private static void registerRefocusModule(ModuleManager moduleManager, final int moduleId,
            final String namespace) {
        moduleManager.registerModule(new ModuleManager.ModuleAgent() {
            @Override
            public int getModuleId() {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera() {
                return true;
            }

            @Override
            public String getScopeNamespace() {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent) {
                return RefocusHelper.createRefocusModule(app);
            }
        });
    }

    private static void registerCaptureIntentModule(ModuleManager moduleManager, final int moduleId,
            final String namespace, final boolean enableCaptureModule) {
        moduleManager.registerModule(new ModuleManager.ModuleAgent() {
            @Override
            public int getModuleId() {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera() {
                return !enableCaptureModule;
            }

            @Override
            public String getScopeNamespace() {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent) {
                if(enableCaptureModule) {
                    try {
                        return new CaptureIntentModule(app, intent, namespace);
                    } catch (OneCameraException ignored) {
                    }
                }
                return new PhotoModule(app);
            }
        });
    }
}
