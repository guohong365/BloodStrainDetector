/*
 * Copyright (C) 2012 The Android Open Source Project
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


package com.uc.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateBeamUrisCallback;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.executor.FifoPriorityThreadPoolExecutor;
import com.uc.android.camera.ButtonManager;
import com.uc.android.camera.CameraModule;
import com.uc.android.camera.FatalErrorHandler;
import com.uc.android.camera.FatalErrorHandlerImpl;
import com.uc.android.camera.OnScreenHint;
import com.uc.android.camera.PhotoModule;
import com.uc.android.camera.SoundPlayer;
import com.uc.android.camera.Storage;
import com.uc.android.camera.app.AppController;
import com.uc.android.camera.app.CameraAppUI;
import com.uc.android.camera.app.CameraController;
import com.uc.android.camera.app.CameraProvider;
import com.uc.android.camera.app.CameraServices;
import com.uc.android.camera.app.CameraServicesImpl;
import com.uc.android.camera.app.LocationManager;
import com.uc.android.camera.app.MemoryManager;
import com.uc.android.camera.app.ModuleManager;
import com.uc.android.camera.app.ModuleManager.ModuleAgent;
import com.uc.android.camera.app.ModuleManagerImpl;
import com.uc.android.camera.app.MotionManager;
import com.uc.android.camera.app.OrientationManager;
import com.uc.android.camera.app.OrientationManagerImpl;
import com.uc.android.camera.device.ActiveCameraDeviceTracker;
import com.uc.android.camera.device.CameraId;
import com.uc.android.camera.module.ModuleController;
import com.uc.android.camera.module.ModulesInfo;
import com.uc.android.camera.one.OneCameraException;
import com.uc.android.camera.one.OneCameraManager;
import com.uc.android.camera.one.OneCameraModule;
import com.uc.android.camera.one.OneCameraOpener;
import com.uc.android.camera.one.config.OneCameraFeatureConfig;
import com.uc.android.camera.one.config.OneCameraFeatureConfigCreator;
import com.uc.android.camera.settings.AppUpgrader;
import com.uc.android.camera.settings.CameraSettingsActivity;
import com.uc.android.camera.settings.Keys;
import com.uc.android.camera.settings.PictureSizeLoader;
import com.uc.android.camera.settings.ResolutionSetting;
import com.uc.android.camera.settings.ResolutionUtil;
import com.uc.android.camera.settings.SettingsManager;
import com.uc.android.camera.ui.MainActivityLayout;
import com.uc.android.camera.ui.PreviewStatusListener;
import com.uc.android.camera.util.ApiHelper;
import com.uc.android.camera.util.CameraUtil;
import com.uc.android.camera.util.PhotoSphereHelper.PanoramaViewHelper;
import com.uc.android.camera.util.ReleaseHelper;
import com.uc.android.ex.camera2.portability.CameraAgent;
import com.uc.android.ex.camera2.portability.CameraAgentFactory;
import com.uc.android.ex.camera2.portability.CameraExceptionHandler;
import com.uc.android.ex.camera2.portability.CameraSettings;
import com.uc.android.util.eventprotos;
import com.uc.android.util.eventprotos.ForegroundEvent.ForegroundSource;
import com.uc.android.util.eventprotos.NavigationChange;
import com.uc.bloodstraindetector.R;
import com.uc.utils.ActivityHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CameraActivity extends ActivityBase
        implements AppController, CameraAgent.CameraOpenCallback{

    private static final int MSG_CLEAR_SCREEN_ON_FLAG = 2;
    private static final long SCREEN_DELAY_MS = 2 * 60 * 1000; // 2 mins.
    /** Load metadata for 10 items ahead of our current. */
    private static final int FILMSTRIP_PRELOAD_AHEAD_ITEMS = 10;
    private static final int PERMISSIONS_ACTIVITY_REQUEST_CODE = 1;
    private static final int PERMISSIONS_RESULT_CODE_OK = 1;
    private static final int PERMISSIONS_RESULT_CODE_FAILED = 2;

    /** Should be used wherever a context is needed. */
    private Context mAppContext;

    /**
     * Camera fatal error handling:
     * 1) Present error dialog to guide users to exit the app.
     * 2) If users hit home button, onPause should just call finish() to exit the app.
     */
    private boolean mCameraFatalError = false;

    /**
     * Whether onResume should reset the view to the preview.
     */
    private boolean mResetToPreviewOnResume = true;

    /**
     * This data adapter is used by FilmStripView.
     */
    private ActiveCameraDeviceTracker mActiveCameraDeviceTracker;
    private OneCameraOpener mOneCameraOpener;
    private OneCameraManager mOneCameraManager;
    private SettingsManager mSettingsManager;
    private ResolutionSetting mResolutionSetting;
    private int mCurrentModeIndex;
    private CameraModule mCurrentModule;
    private ModuleManagerImpl mModuleManager;
    /** Whether the filmstrip fully covers the preview. */
    private boolean mFilmstripCoversPreview = false;
    private int mResultCodeForTesting;
    private Intent mResultDataForTesting;
    private OnScreenHint mStorageHint;
    private final Object mStorageSpaceLock = new Object();
    private long mStorageSpaceBytes = Storage.LOW_STORAGE_THRESHOLD_BYTES;
    private boolean mAutoRotateScreen;
    private OrientationManagerImpl mOrientationManager;
    private LocationManager mLocationManager;
    private ButtonManager mButtonManager;
    private Handler mMainHandler;
    private PanoramaViewHelper mPanoramaViewHelper;
    private ActionBar mActionBar;
    private boolean mIsActivityRunning = false;
    private FatalErrorHandler mFatalErrorHandler;
    private boolean mHasCriticalPermissions;

    private final Uri[] mNfcPushUris = new Uri[1];
    private CameraController mCameraController;
    private boolean mPaused;
    private CameraAppUI mCameraAppUI;

    /** Can be used to play custom sounds. */
    private SoundPlayer mSoundPlayer;

    /** Holds configuration for various OneCamera features. */
    private OneCameraFeatureConfig mFeatureConfig;

    private static final int LIGHTS_OUT_DELAY_MS = 4000;
    private final int BASE_SYS_UI_VISIBILITY =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    private final Runnable mLightsOutRunnable = () -> getWindow().getDecorView().setSystemUiVisibility(
            BASE_SYS_UI_VISIBILITY | View.SYSTEM_UI_FLAG_LOW_PROFILE);
    private MemoryManager mMemoryManager;
    private MotionManager mMotionManager;

    @Override
    public CameraAppUI getCameraAppUI() {
        return mCameraAppUI;
    }

    @Override
    public ModuleManager getModuleManager() {
        return mModuleManager;
    }

    /**
     * Close activity when secure app passes lock screen or screen turns
     * off.
     */
    private final BroadcastReceiver mShutdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    /**
     * Whether the screen is kept turned on.
     */
    private boolean mKeepScreenOn;
    private int mLastLayoutOrientation;

    @Override
    public void onCameraOpened(CameraAgent.CameraProxy camera) {
        Log.v(TAG, "onCameraOpened");
        if (mPaused) {
            // We've paused, but just asynchronously opened the camera. Close it
            // because we should be releasing the camera when paused to allow
            // other apps to access it.
            Log.v(TAG, "received onCameraOpened but activity is paused, closing Camera");
            mCameraController.closeCamera(false);
            return;
        }

        if (!mModuleManager.getModuleAgent(mCurrentModeIndex).requestAppForCamera()) {
            // We shouldn't be here. Just close the camera and leave.
            mCameraController.closeCamera(false);
            throw new IllegalStateException("Camera opened but the module shouldn't be " +
                    "requesting");
        }
        if (mCurrentModule != null) {
            resetExposureCompensationToDefault(camera);
            try {
                mCurrentModule.onCameraAvailable(camera);
            } catch (RuntimeException ex) {
                Log.e(TAG, "Error connecting to camera", ex);
                mFatalErrorHandler.onCameraOpenFailure();
            }
        } else {
            Log.v(TAG, "mCurrentModule null, not invoking onCameraAvailable");
        }
        Log.v(TAG, "invoking onChangeCamera");
        mCameraAppUI.onChangeCamera();
    }

    private void resetExposureCompensationToDefault(CameraAgent.CameraProxy camera) {
        // Reset the exposure compensation before handing the camera to module.
        CameraSettings cameraSettings = camera.getSettings();
        cameraSettings.setExposureCompensationIndex(0);
        camera.applySettings(cameraSettings);
    }

    @Override
    public void onCameraDisabled(int cameraId) {
        Log.w(TAG, "Camera disabled: " + cameraId);
        mFatalErrorHandler.onCameraDisabledFailure();
    }

    @Override
    public void onDeviceOpenFailure(int cameraId, String info) {
        Log.w(TAG, "Camera open failure: " + info);
        mFatalErrorHandler.onCameraOpenFailure();
    }

    @Override
    public void onDeviceOpenedAlready(int cameraId, String info) {
        Log.w(TAG, "Camera open already: " + cameraId + "," + info);
        mFatalErrorHandler.onGenericCameraAccessFailure();
    }

    @Override
    public void onReconnectionFailure(CameraAgent mgr, String info) {
        Log.w(TAG, "Camera reconnection failure:" + info);
        mFatalErrorHandler.onCameraReconnectFailure();
    }

    private static class MainHandler extends Handler {
        final WeakReference<CameraActivity> mActivity;

        MainHandler(CameraActivity activity, Looper looper) {
            super(looper);
            mActivity = new WeakReference<CameraActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CameraActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {

                case MSG_CLEAR_SCREEN_ON_FLAG: {
                    if (!activity.mPaused) {
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                    break;
                }
            }
        }
    }

    private void setupNfcBeamPush() {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(mAppContext);
        if (adapter == null) {
            return;
        }

        if (!ApiHelper.HAS_SET_BEAM_PUSH_URIS) {
            // Disable beaming
            adapter.setNdefPushMessage(null, CameraActivity.this);
            return;
        }

        adapter.setBeamPushUris(null, CameraActivity.this);
        adapter.setBeamPushUrisCallback(new CreateBeamUrisCallback() {
            @Override
            public Uri[] createBeamUris(NfcEvent event) {
                return mNfcPushUris;
            }
        }, CameraActivity.this);
    }

    @Override
    public Context getAndroidContext() {
        return mAppContext;
    }

    @Override
    public OneCameraFeatureConfig getCameraFeatureConfig() {
        return mFeatureConfig;
    }

    @Override
    public Dialog createDialog() {
        return new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void launchActivityByIntent(Intent intent) {
        // Starting from L, we prefer not to start edit activity within camera's task.
        mResetToPreviewOnResume = false;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        startActivity(intent);
    }

    @Override
    public int getCurrentModuleIndex() {
        return mCurrentModeIndex;
    }

    @Override
    public String getModuleScope() {
        ModuleAgent agent = mModuleManager.getModuleAgent(mCurrentModeIndex);
        return SettingsManager.getModuleSettingScope(agent.getScopeNamespace());
    }

    @Override
    public String getCameraScope() {
        // if an unopen camera i.e. negative ID is returned, which we've observed in
        // some automated scenarios, just return it as a valid separate scope
        // this could cause user issues, so log a stack trace noting the call path
        // which resulted in this scenario.

        CameraId cameraId =  mCameraController.getCurrentCameraId();

        if(cameraId == null) {
            Log.e(TAG,  "Retrieving Camera Setting Scope with -1");
            return SettingsManager.getCameraSettingScope("-1");
        }

        return SettingsManager.getCameraSettingScope(cameraId.getValue());
    }

    @Override
    public ModuleController getCurrentModuleController() {
        return mCurrentModule;
    }

    @Override
    public SurfaceTexture getPreviewBuffer() {
        // TODO: implement this
        return null;
    }

    @Override
    public void onPreviewReadyToStart() {
        mCameraAppUI.onPreviewReadyToStart();
    }

    @Override
    public void onPreviewStarted() {
        mCameraAppUI.onPreviewStarted();
    }

    @Override
    public void addPreviewAreaSizeChangedListener(
            PreviewStatusListener.PreviewAreaChangedListener listener) {
        mCameraAppUI.addPreviewAreaChangedListener(listener);
    }

    @Override
    public void removePreviewAreaSizeChangedListener(
            PreviewStatusListener.PreviewAreaChangedListener listener) {
        mCameraAppUI.removePreviewAreaChangedListener(listener);
    }

    @Override
    public void setupOneShotPreviewListener() {
        mCameraController.setOneShotPreviewCallback(mMainHandler,
                new CameraAgent.CameraPreviewDataCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, CameraAgent.CameraProxy camera) {
                        mCurrentModule.onPreviewInitialDataReceived();
                        mCameraAppUI.onNewPreviewFrame();
                    }
                }
        );
    }

    @Override
    public void updatePreviewAspectRatio(float aspectRatio) {
        mCameraAppUI.updatePreviewAspectRatio(aspectRatio);
    }

    @Override
    public void updatePreviewTransformFullscreen(Matrix matrix, float aspectRatio) {
        mCameraAppUI.updatePreviewTransformFullscreen(matrix, aspectRatio);
    }

    @Override
    public RectF getFullscreenRect() {
        return mCameraAppUI.getFullscreenRect();
    }

    @Override
    public void updatePreviewTransform(Matrix matrix) {
        mCameraAppUI.updatePreviewTransform(matrix);
    }

    @Override
    public void setPreviewStatusListener(PreviewStatusListener previewStatusListener) {
        mCameraAppUI.setPreviewStatusListener(previewStatusListener);
    }

    @Override
    public FrameLayout getModuleLayoutRoot() {
        return mCameraAppUI.getModuleRootView();
    }

    @Override
    public void setShutterEventsListener(ShutterEventsListener listener) {
        // TODO: implement this
    }

    @Override
    public void setShutterEnabled(boolean enabled) {
        mCameraAppUI.setShutterButtonEnabled(enabled);
    }

    @Override
    public boolean isShutterEnabled() {
        return mCameraAppUI.isShutterButtonEnabled();
    }

    @Override
    public void startFlashAnimation(boolean shortFlash) {
        mCameraAppUI.startFlashAnimation(shortFlash);
    }

    @Override
    public void startPreCaptureAnimation() {
        // TODO: implement this
    }

    @Override
    public void cancelPreCaptureAnimation() {
        // TODO: implement this
    }

    @Override
    public void startPostCaptureAnimation() {
        // TODO: implement this
    }

    @Override
    public void startPostCaptureAnimation(Bitmap thumbnail) {
        // TODO: implement this
    }

    @Override
    public void cancelPostCaptureAnimation() {
        // TODO: implement this
    }

    @Override
    public OrientationManager getOrientationManager() {
        return mOrientationManager;
    }

    @Override
    public LocationManager getLocationManager() {
        return mLocationManager;
    }

    @Override
    public void lockOrientation() {
        if (mOrientationManager != null) {
            mOrientationManager.lockOrientation();
        }
    }

    @Override
    public void unlockOrientation() {
        if (mOrientationManager != null) {
            mOrientationManager.unlockOrientation();
        }
    }

    @Override
    public void notifyNewMedia(Uri uri) {
        // TODO: This method is running on the main thread. Also we should get
        // rid of that AsyncTask.
        updateStorageSpaceAndHint(null);
    }

    @Override
    public void enableKeepScreenOn(boolean enabled) {
        if (mPaused) {
            return;
        }

        mKeepScreenOn = enabled;
        if (mKeepScreenOn) {
            mMainHandler.removeMessages(MSG_CLEAR_SCREEN_ON_FLAG);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            keepScreenOnForAWhile();
        }
    }

    @Override
    public CameraProvider getCameraProvider() {
        return mCameraController;
    }

    @Override
    public OneCameraOpener getCameraOpener() {
        return mOneCameraOpener;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isCaptureIntent() {
        if (MediaStore.ACTION_VIDEO_CAPTURE.equals(getIntent().getAction())
                || MediaStore.ACTION_IMAGE_CAPTURE.equals(getIntent().getAction())
                || MediaStore.ACTION_IMAGE_CAPTURE_SECURE.equals(getIntent().getAction())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Note: Make sure this callback is unregistered properly when the activity
     * is destroyed since we're otherwise leaking the Activity reference.
     */
    private final CameraExceptionHandler.CameraExceptionCallback mCameraExceptionCallback
        = new CameraExceptionHandler.CameraExceptionCallback() {
                @Override
                public void onCameraError(int errorCode) {
                    // Not a fatal error. only do Log.e().
                    Log.e(TAG, "Camera error callback. error=" + errorCode);
                }
                @Override
                public void onCameraException(
                        RuntimeException ex, String commandHistory, int action, int state) {
                    Log.e(TAG, "Camera Exception", ex);
                    onFatalError();
                }
                @Override
                public void onDispatchThreadException(RuntimeException ex) {
                    Log.e(TAG, "DispatchThread Exception", ex);

                    onFatalError();
                }
                private void onFatalError() {
                    if (mCameraFatalError) {
                        return;
                    }
                    mCameraFatalError = true;

                    // If the activity receives exception during onPause, just exit the app.
                    if (mPaused && !isFinishing()) {
                        Log.e(TAG, "Fatal error during onPause, call Activity.finish()");
                        finish();
                    } else {
                        mFatalErrorHandler.handleFatalError(FatalErrorHandler.Reason.CANNOT_CONNECT_TO_CAMERA);
                    }
                }
            };

    @Override
    public void onNewIntentTasks(Intent intent) {
        onModeSelected(getModeIndex());
    }

    @Override
    public void onCreateTasks(Bundle state) {
        ActivityHelper.forceLayoutOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //hideStatusBarAndActionBar();
        mAppContext = getApplicationContext();
        mMainHandler = new MainHandler(this, getMainLooper());
        mLocationManager = new LocationManager(mAppContext);
        mOrientationManager = new OrientationManagerImpl(this, mMainHandler);
        mSettingsManager = getServices().getSettingsManager();
        mSoundPlayer = new SoundPlayer(mAppContext);
        mFeatureConfig = OneCameraFeatureConfigCreator.createDefault(getContentResolver(),
                getServices().getMemoryManager());
        mFatalErrorHandler = new FatalErrorHandlerImpl(this);
        checkPermissions();
        if (!mHasCriticalPermissions) {
            Log.v(TAG, "onCreate: Missing critical permissions.");
            finish();
            return;
        }
        if (!Glide.isSetup()) {
            Context context = getAndroidContext();
            Glide.setup(new GlideBuilder(context)
                .setDecodeFormat(DecodeFormat.ALWAYS_ARGB_8888)
                .setResizeService(new FifoPriorityThreadPoolExecutor(2)));

            Glide glide = Glide.get(context);

            // As a camera we will use a large amount of memory
            // for displaying images.
            glide.setMemoryCategory(MemoryCategory.HIGH);
        }
        mActiveCameraDeviceTracker = ActiveCameraDeviceTracker.instance();
        try {
            mOneCameraOpener = OneCameraModule.provideOneCameraOpener(
                    mFeatureConfig,
                    mAppContext,
                    mActiveCameraDeviceTracker,
                    ResolutionUtil.getDisplayMetrics(this));
            mOneCameraManager = OneCameraModule.provideOneCameraManager();
        } catch (OneCameraException e) {
            // Log error and continue start process while showing error dialog..
            Log.e(TAG, "Creating camera manager failed.", e);
            mFatalErrorHandler.onGenericCameraAccessFailure();
        }

        try {
            mCameraController = new CameraController(mAppContext, this, mMainHandler,
                    CameraAgentFactory.getAndroidCameraAgent(mAppContext,
                            CameraAgentFactory.CameraApi.API_1),
                    CameraAgentFactory.getAndroidCameraAgent(mAppContext,
                            CameraAgentFactory.CameraApi.AUTO),
                    mActiveCameraDeviceTracker);
            mCameraController.setCameraExceptionHandler(
                    new CameraExceptionHandler(mCameraExceptionCallback, mMainHandler));
        } catch (AssertionError e) {
            Log.e(TAG, "Creating camera controller failed.", e);
            mFatalErrorHandler.onGenericCameraAccessFailure();
        }

        // TODO: Try to move all the resources allocation to happen as soon as
        // possible so we can call module.init() at the earliest time.
        mModuleManager = new ModuleManagerImpl();

        ModulesInfo.setupModules(mAppContext, mModuleManager, mFeatureConfig);

        AppUpgrader appUpgrader = new AppUpgrader(this);
        appUpgrader.upgrade(mSettingsManager);

        // Make sure the picture sizes are correctly cached for the current OS
        // version.
        try {
            (new PictureSizeLoader(mAppContext)).computePictureSizes();
        } catch (AssertionError e) {
            Log.e(TAG, "Creating camera controller failed.", e);
            mFatalErrorHandler.onGenericCameraAccessFailure();
        }
        Keys.setDefaults(mSettingsManager, mAppContext);

        mResolutionSetting = new ResolutionSetting(mSettingsManager, mOneCameraManager,
                getContentResolver());

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        // We suppress this flag via theme when drawing the system preview
        // background, but once we create activity here, reactivate to the
        // default value. The default is important for L, we don't want to
        // change app behavior, just starting background drawable layout.
        if (ApiHelper.isLOrHigher()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        //setFullScreenMode(FULL_SCREEN_MODE.FULL_SCREEN_HIDE_ALL);
        setContentView(R.layout.activity_camera);
        // A window background is set in styles.xml for the system to show a
        // drawable background with gray color and camera icon before the
        // activity is created. We set the background to null here to prevent
        // overdraw, all views must take care of drawing backgrounds if
        // necessary. This call to setBackgroundDrawable must occur after
        // setContentView, otherwise a background may be set again from the
        // style.
        getWindow().setBackgroundDrawable(null);

        mActionBar = getActionBar();
        // set actionbar background to 100% or 50% transparent
        if(mActionBar!=null) {
            mActionBar.setBackgroundDrawable(new ColorDrawable(0x00000000));
            mActionBar.hide();
        }
        if (ApiHelper.HAS_ROTATION_ANIMATION) {
            setRotationAnimation();
        }

        // Check if this is in the secure camera mode.
        Intent intent = getIntent();
        String action = intent.getAction();
        mCameraAppUI = new CameraAppUI(this,
                (MainActivityLayout) findViewById(R.id.activity_root_view), isCaptureIntent());
        mPanoramaViewHelper = new PanoramaViewHelper(this);
        mPanoramaViewHelper.onCreate();

        ContentResolver appContentResolver = mAppContext.getContentResolver();

        if (mSettingsManager.getBoolean(SettingsManager.SCOPE_GLOBAL,
                                        Keys.KEY_SHOULD_SHOW_REFOCUS_VIEWER_CLING)) {
            mCameraAppUI.setupClingForViewer(CameraAppUI.BottomPanel.VIEWER_REFOCUS);
        }

        setModuleFromModeIndex(getModeIndex());

        mCameraAppUI.prepareModuleUI();
        mCurrentModule.init(this, isCaptureIntent());
        setupNfcBeamPush();
        mMemoryManager = getServices().getMemoryManager();

        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                HashMap memoryData = mMemoryManager.queryMemory();
            }
        });

        mMotionManager = getServices().getMotionManager();

    }

    /**
     * Get the current mode index from the Intent or from persistent
     * settings.
     */
    private int getModeIndex() {
        int modeIndex = -1;
        int photoIndex = getResources().getInteger(R.integer.camera_mode_photo);
        int captureIntentIndex =getResources().getInteger(R.integer.camera_mode_capture_intent);
        String intentAction = getIntent().getAction();
        if (MediaStore.ACTION_IMAGE_CAPTURE.equals(intentAction)
                || MediaStore.ACTION_IMAGE_CAPTURE_SECURE.equals(intentAction)) {
            // Capture intent.
            modeIndex = captureIntentIndex;
        } else if (MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA.equals(intentAction)
                ||MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE.equals(intentAction)
                || MediaStore.ACTION_IMAGE_CAPTURE_SECURE.equals(intentAction)) {
            modeIndex = mSettingsManager.getInteger(SettingsManager.SCOPE_GLOBAL,
                Keys.KEY_CAMERA_MODULE_LAST_USED);

            // For upgraders who have not seen the aspect ratio selection screen,
            // we need to drop them back in the photo module and have them select
            // aspect ratio.
            // TODO: Move this to SettingsManager as an upgrade procedure.
            if (!mSettingsManager.getBoolean(SettingsManager.SCOPE_GLOBAL,
                    Keys.KEY_USER_SELECTED_ASPECT_RATIO)) {
                modeIndex = photoIndex;
            }
        } else {
            // If the activity has not been started using an explicit intent,
            // read the module index from the last time the user changed modes
            modeIndex = mSettingsManager.getInteger(SettingsManager.SCOPE_GLOBAL,
                                                    Keys.KEY_STARTUP_MODULE_INDEX);
            if (modeIndex < 0) {
                modeIndex = photoIndex;
            }
        }
        return modeIndex;
    }

    /**
     * Call this whenever the mode drawer or filmstrip change the visibility
     * state.
     */
    private void updatePreviewVisibility() {
        if (mCurrentModule == null) {
            return;
        }
        int visibility = getPreviewVisibility();
        mCameraAppUI.onPreviewVisiblityChanged(visibility);
        updatePreviewRendering(visibility);
        mCurrentModule.onPreviewVisibilityChanged(visibility);
    }

    private void updatePreviewRendering(int visibility) {
        if (visibility == ModuleController.VISIBILITY_HIDDEN) {
            mCameraAppUI.pausePreviewRendering();
        } else {
            mCameraAppUI.resumePreviewRendering();
        }
    }

    private int getPreviewVisibility() {
        if (mFilmstripCoversPreview) {
            return ModuleController.VISIBILITY_HIDDEN;
        } else {
            return ModuleController.VISIBILITY_VISIBLE;
        }
    }

    private void setRotationAnimation() {
        int rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_ROTATE;
        rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_CROSSFADE;
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.rotationAnimation = rotationAnimation;
        win.setAttributes(winParams);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (!isFinishing()) {
            keepScreenOnForAWhile();
        }
    }

    @Override
    public void onPauseTasks() {
        /*
         * Save the last module index after all secure camera and icon launches,
         * not just on mode switches.
         *
         * Right now we exclude capture intents from this logic, because we also
         * ignore the cross-Activity recovery logic in onStart for capture intents.
         */
        if (!isCaptureIntent()) {
            mSettingsManager.set(SettingsManager.SCOPE_GLOBAL,
                                 Keys.KEY_STARTUP_MODULE_INDEX,
                mCurrentModeIndex);
        }

        mPaused = true;

        // Delete photos that are pending deletion
        mCurrentModule.pause();
        mOrientationManager.pause();
        mPanoramaViewHelper.onPause();
        resetScreenOn();

        mMotionManager.stop();

        // Always stop recording location when paused. Resume will start
        // location recording again if the location setting is on.
        mLocationManager.recordLocation(false);

        // Camera is in fatal state. A fatal dialog is presented to users, but users just hit home
        // button. Let's just kill the process.
        if (mCameraFatalError && !isFinishing()) {
            Log.v(TAG, "onPause when camera is in fatal state, call Activity.finish()");
            finish();
        } else {
            // Close the camera and wait for the operation done.
            Log.v(TAG, "onPause closing camera");
            if (mCameraController != null) {
                mCameraController.closeCamera(true);
            }
        }
    }

    @Override
    public void onResumeTasks() {
        mPaused = false;
        checkPermissions();
        if (!mHasCriticalPermissions) {
            Log.v(TAG, "onResume: Missing critical permissions.");
            finish();
            return;
        }
        hideNavigationBarAndStatusBar();
        // In secure mode from lockscreen, we go straight to camera and will
        // show first run dialog next time user enters launcher.
        Log.v(TAG, "in secure mode, skipping first run dialog check");
        resume();
    }

    /**
     * Checks if any of the needed Android runtime permissions are missing.
     * If they are, then launch the permissions activity under one of the following conditions:
     * a) The permissions dialogs have not run yet. We will ask for permission only once.
     * b) If the missing permissions are critical to the app running, we will display a fatal error dialog.
     * Critical permissions are: camera, microphone and storage. The app cannot run without them.
     * Non-critical permission is location.
     */
    private void checkPermissions() {
        if (!ApiHelper.isMOrHigher()) {
            Log.v(TAG, "not running on M, skipping permission checks");
            mHasCriticalPermissions = true;
            return;
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mHasCriticalPermissions = true;
        } else {
            mHasCriticalPermissions = false;
        }

        if ((checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                !mSettingsManager.getBoolean(SettingsManager.SCOPE_GLOBAL, Keys.KEY_HAS_SEEN_PERMISSIONS_DIALOGS)) ||
                !mHasCriticalPermissions) {
            Intent intent = new Intent(this, PermissionsActivity.class);
            startActivity(intent);
            finish();
        }
    }

     private void resume() {
        Log.v(TAG, "Build info: " + Build.DISPLAY);
        updateStorageSpaceAndHint(null);

        mLastLayoutOrientation = getResources().getConfiguration().orientation;

        // TODO: Handle this in OrientationManager.
        // Auto-rotate off
        if (Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 0) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            mAutoRotateScreen = false;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            mAutoRotateScreen = true;
        }

        // Foreground event logging.  ACTION_STILL_IMAGE_CAMERA and
        // INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE are double logged due to
        // lockscreen onResume->onPause->onResume sequence.
        int source;
        String action = getIntent().getAction();
        if (action == null) {
            source = ForegroundSource.UNKNOWN_SOURCE;
        } else {
            switch (action) {
                case MediaStore.ACTION_IMAGE_CAPTURE:
                    source = ForegroundSource.ACTION_IMAGE_CAPTURE;
                    break;
                case MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA:
                    // was UNKNOWN_SOURCE in Fishlake.
                    source = ForegroundSource.ACTION_STILL_IMAGE_CAMERA;
                    break;
                case MediaStore.INTENT_ACTION_VIDEO_CAMERA:
                    // was UNKNOWN_SOURCE in Fishlake.
                    source = ForegroundSource.ACTION_VIDEO_CAMERA;
                    break;
                case MediaStore.ACTION_VIDEO_CAPTURE:
                    source = ForegroundSource.ACTION_VIDEO_CAPTURE;
                    break;
                case MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE:
                    // was ACTION_IMAGE_CAPTURE_SECURE in Fishlake.
                    source = ForegroundSource.ACTION_STILL_IMAGE_CAMERA_SECURE;
                    break;
                case MediaStore.ACTION_IMAGE_CAPTURE_SECURE:
                    source = ForegroundSource.ACTION_IMAGE_CAPTURE_SECURE;
                    break;
                case Intent.ACTION_MAIN:
                    source = ForegroundSource.ACTION_MAIN;
                    break;
                default:
                    source = ForegroundSource.UNKNOWN_SOURCE;
                    break;
            }
        }

         if (ApiHelper.isLOrHigher()) {
            // hide the up affordance for L devices, it's not very Materially
            if(mActionBar!=null){
                mActionBar.setDisplayShowHomeEnabled(false);
            }
        }

        mOrientationManager.resume();

        mCurrentModule.hardResetSettings(mSettingsManager);
        mCurrentModule.resume();

        setSwipingEnabled(true);
        // Default is showing the preview, unless disabled by explicitly
        // starting an activity we want to return from to the filmstrip rather
        // than the preview.
        mResetToPreviewOnResume = true;
        keepScreenOnForAWhile();

        // Lights-out mode at all times.
        final View rootView = findViewById(R.id.activity_root_view);
        mLightsOutRunnable.run();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
              new OnSystemUiVisibilityChangeListener() {
                  @Override
                  public void onSystemUiVisibilityChange(int visibility) {
                      mMainHandler.removeCallbacks(mLightsOutRunnable);
                      mMainHandler.postDelayed(mLightsOutRunnable, LIGHTS_OUT_DELAY_MS);
                  }
              });

        mPanoramaViewHelper.onResume();
        ReleaseHelper.showReleaseInfoDialogOnStart(this, mSettingsManager);
        // Enable location recording if the setting is on.
        final boolean locationRecordingEnabled =
                mSettingsManager.getBoolean(SettingsManager.SCOPE_GLOBAL, Keys.KEY_RECORD_LOCATION);
        mLocationManager.recordLocation(locationRecordingEnabled);

        final int previewVisibility = getPreviewVisibility();
        updatePreviewRendering(previewVisibility);

        mMotionManager.start();
    }

    @Override
    public void onStartTasks() {
        mIsActivityRunning = true;
        mPanoramaViewHelper.onStart();

        /*
         * If we're starting after launching a different Activity (lockscreen),
         * we need to use the last mode used in the other Activity, and
         * not the old one from this Activity.
         *
         * This needs to happen before CameraAppUI.resume() in order to set the
         * mode cover icon to the actual last mode used.
         *
         * Right now we exclude capture intents from this logic.
         */
        int modeIndex = getModeIndex();
        if (!isCaptureIntent() && mCurrentModeIndex != modeIndex) {
            onModeSelected(modeIndex);
        }

        if (mResetToPreviewOnResume) {
            mCameraAppUI.resume();
            mResetToPreviewOnResume = false;
        }
    }

    @Override
    protected void onStopTasks() {
        mIsActivityRunning = false;
        mPanoramaViewHelper.onStop();

        mLocationManager.disconnect();
    }

    @Override
    public void onDestroyTasks() {
        // Ensure anything that checks for "isPaused" returns true.
        mPaused = true;

        mSettingsManager.removeAllListeners();
        if (mCameraController != null) {
            mCameraController.removeCallbackReceiver();
            mCameraController.setCameraExceptionHandler(null);
        }
        if (mCameraAppUI != null) {
            mCameraAppUI.onDestroy();
        }
        mCameraController = null;
        mSettingsManager = null;
        mOrientationManager = null;
        mButtonManager = null;
        if (mSoundPlayer != null) {
          mSoundPlayer.release();
        }
        CameraAgentFactory.recycle(CameraAgentFactory.CameraApi.API_1);
        CameraAgentFactory.recycle(CameraAgentFactory.CameraApi.AUTO);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        Log.v(TAG, "onConfigurationChanged");
        if (config.orientation == Configuration.ORIENTATION_UNDEFINED) {
            return;
        }

        if (mLastLayoutOrientation != config.orientation) {
            mLastLayoutOrientation = config.orientation;
            mCurrentModule.onLayoutOrientationChanged(
                    mLastLayoutOrientation == Configuration.ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (mCurrentModule.onKeyDown(keyCode, event)) {
                return true;
            }
            // Prevent software keyboard or voice search from showing up.
            if (keyCode == KeyEvent.KEYCODE_SEARCH
                    || keyCode == KeyEvent.KEYCODE_MENU) {
                if (event.isLongPress()) {
                    return true;
                }
            }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
            // If a module is in the middle of capture, it should
            // consume the key event.
            if (mCurrentModule.onKeyUp(keyCode, event)) {
                return true;
            }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (!mCameraAppUI.onBackPressed()) {
            if (!mCurrentModule.onBackPressed()) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean isAutoRotateScreen() {
        // TODO: Move to OrientationManager.
        return mAutoRotateScreen;
    }

    public long getStorageSpaceBytes() {
        synchronized (mStorageSpaceLock) {
            return mStorageSpaceBytes;
        }
    }

    protected interface OnStorageUpdateDoneListener {
        public void onStorageUpdateDone(long bytes);
    }

    public void updateStorageSpaceAndHint(final OnStorageUpdateDoneListener callback) {
        /*
         * We execute disk operations on a background thread in order to
         * free up the UI thread.  Synchronizing on the lock below ensures
         * that when getStorageSpaceBytes is called, the main thread waits
         * until this method has completed.
         *
         * However, .execute() does not ensure this execution block will be
         * run right away (.execute() schedules this AsyncTask for sometime
         * in the future. executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
         * tries to execute the task in parellel with other AsyncTasks, but
         * there's still no guarantee).
         * e.g. don't call this then immediately call getStorageSpaceBytes().
         * Instead, pass in an OnStorageUpdateDoneListener.
         */
        (new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void ... arg) {
                synchronized (mStorageSpaceLock) {
                    mStorageSpaceBytes = Storage.getAvailableSpace();
                    return mStorageSpaceBytes;
                }
            }

            @Override
            protected void onPostExecute(Long bytes) {
                updateStorageHint(bytes);
                // This callback returns after I/O to check disk, so we could be
                // pausing and shutting down. If so, don't bother invoking.
                if (callback != null && !mPaused) {
                    callback.onStorageUpdateDone(bytes);
                } else {
                    Log.v(TAG, "ignoring storage callback after activity pause");
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void updateStorageHint(long storageSpace) {
        if (!mIsActivityRunning) {
            return;
        }

        String message = null;
        if (storageSpace == Storage.UNAVAILABLE) {
            message = getString(R.string.no_storage);
        } else if (storageSpace == Storage.PREPARING) {
            message = getString(R.string.preparing_sd);
        } else if (storageSpace == Storage.UNKNOWN_SIZE) {
            message = getString(R.string.access_sd_fail);
        } else if (storageSpace <= Storage.LOW_STORAGE_THRESHOLD_BYTES) {
            message = getString(R.string.spaceIsLow_content);
        }

        if (message != null) {
            Log.w(TAG, "Storage warning: " + message);
            if (mStorageHint == null) {
                mStorageHint = OnScreenHint.makeText(CameraActivity.this, message);
            } else {
                mStorageHint.setText(message);
            }
            mStorageHint.show();

            // Disable all user interactions,
            mCameraAppUI.setDisableAllUserInteractions(true);
        } else if (mStorageHint != null) {
            mStorageHint.cancel();
            mStorageHint = null;

            // Re-enable all user interactions.
            mCameraAppUI.setDisableAllUserInteractions(false);
        }
    }

    public void setResultEx(int resultCode) {
        mResultCodeForTesting = resultCode;
        setResult(resultCode);
    }

    public void setResultEx(int resultCode, Intent data) {
        mResultCodeForTesting = resultCode;
        mResultDataForTesting = data;
        setResult(resultCode, data);
    }

    public int getResultCode() {
        return mResultCodeForTesting;
    }

    public Intent getResultData() {
        return mResultDataForTesting;
    }

    @Override
    public boolean isPaused() {
        return mPaused;
    }

    @Override
    public int getPreferredChildModeIndex(int modeIndex) {
        return modeIndex;
    }

    public void onModeSelected(int modeIndex) {
        if (mCurrentModeIndex == modeIndex) {
            return;
        }
        // Record last used camera mode for quick switching
        if (modeIndex == getResources().getInteger(R.integer.camera_mode_photo)
                || modeIndex == getResources().getInteger(R.integer.camera_mode_gcam)) {
            mSettingsManager.set(SettingsManager.SCOPE_GLOBAL,
                                 Keys.KEY_CAMERA_MODULE_LAST_USED,
                                 modeIndex);
        }

        closeModule(mCurrentModule);

        // Select the correct module index from the mode switcher index.
        modeIndex = getPreferredChildModeIndex(modeIndex);
        setModuleFromModeIndex(modeIndex);

        mCameraAppUI.resetBottomControls(mCurrentModule, modeIndex);
        mCameraAppUI.addShutterListener(mCurrentModule);
        openModule(mCurrentModule);
        // Store the module index so we can use it the next time the Camera
        // starts up.
        mSettingsManager.set(SettingsManager.SCOPE_GLOBAL,
                             Keys.KEY_STARTUP_MODULE_INDEX, modeIndex);
    }

    /**
     * Shows the settings dialog.
     */
    @Override
    public void onSettingsSelected() {
        Intent intent = new Intent(this, CameraSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void freezeScreenUntilPreviewReady() {
        mCameraAppUI.freezeScreenUntilPreviewReady();
    }

    @Override
    public int getModuleId(int modeIndex) {
        ModuleManagerImpl.ModuleAgent agent = mModuleManager.getModuleAgent(modeIndex);
        if (agent == null) {
            return -1;
        }
        return agent.getModuleId();
    }

    /**
     * Sets the mCurrentModuleIndex, creates a new module instance for the given
     * index an sets it as mCurrentModule.
     */
    private void setModuleFromModeIndex(int modeIndex) {
        ModuleManagerImpl.ModuleAgent agent = mModuleManager.getModuleAgent(modeIndex);
        if (agent == null) {
            return;
        }
        if (!agent.requestAppForCamera()) {
            mCameraController.closeCamera(true);
        }
        mCurrentModeIndex = agent.getModuleId();
        mCurrentModule = (CameraModule) agent.createModule(this, getIntent());
    }

    @Override
    public SettingsManager getSettingsManager() {
        return mSettingsManager;
    }

    @Override
    public ResolutionSetting getResolutionSetting() {
        return mResolutionSetting;
    }

    @Override
    public CameraServices getServices() {
        return CameraServicesImpl.instance();
    }

    @Override
    public FatalErrorHandler getFatalErrorHandler() {
        return mFatalErrorHandler;
    }

    public List<String> getSupportedModeNames() {
        List<Integer> indices = mModuleManager.getSupportedModeIndexList();
        List<String> supported = new ArrayList<String>();

        for (Integer modeIndex : indices) {
            String name = CameraUtil.getCameraModeText(modeIndex, mAppContext);
            if (name != null && !name.equals("")) {
                supported.add(name);
            }
        }
        return supported;
    }

    @Override
    public ButtonManager getButtonManager() {
        if (mButtonManager == null) {
            mButtonManager = new ButtonManager(this);
        }
        return mButtonManager;
    }

    @Override
    public SoundPlayer getSoundPlayer() {
        return mSoundPlayer;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filmstrip_context_menu, menu);
    }

    /**
     * Returns what UI mode (capture mode or filmstrip) we are in.
     * Returned number one of {@link eventprotos.NavigationChange.Mode}
     */
    private int currentUserInterfaceMode() {
        int mode = NavigationChange.Mode.UNKNOWN_MODE;
        if (mCurrentModeIndex == getResources().getInteger(R.integer.camera_mode_photo)) {
            mode = NavigationChange.Mode.PHOTO_CAPTURE;
        }
        if (mCurrentModeIndex == getResources().getInteger(R.integer.camera_mode_refocus)) {
            mode = NavigationChange.Mode.LENS_BLUR;
        }
        if (mCurrentModeIndex == getResources().getInteger(R.integer.camera_mode_gcam)) {
            mode = NavigationChange.Mode.HDR_PLUS;
        }
        if (mCurrentModeIndex == getResources().getInteger(R.integer.camera_mode_photosphere)) {
            mode = NavigationChange.Mode.PHOTO_SPHERE;
        }
        if (mCurrentModeIndex == getResources().getInteger(R.integer.camera_mode_panorama)) {
            mode = NavigationChange.Mode.PANORAMA;
        }
        return mode;
    }

    private void openModule(CameraModule module) {
        module.init(this, isCaptureIntent());
        module.hardResetSettings(mSettingsManager);

        if (!mPaused) {
            module.resume();
            updatePreviewVisibility();
        }
    }

    private void closeModule(CameraModule module) {
        module.pause();
        mCameraAppUI.clearModuleUI();
    }

    /**
     * Enable/disable swipe-to-filmstrip. Will always disable swipe if in
     * capture intent.
     *
     * @param enable {@code true} to enable swipe.
     */
    public void setSwipingEnabled(boolean enable) {
        // TODO: Bring back the functionality.
        if (isCaptureIntent()) {
            // lockPreview(true);
        } else {
            // lockPreview(!enable);
        }
    }

    public long getAutoFocusTime() {
        return (mCurrentModule instanceof PhotoModule) ?
                ((PhotoModule) mCurrentModule).mAutoFocusTime : -1;
    }

    public long getShutterLag() {
        return (mCurrentModule instanceof PhotoModule) ?
                ((PhotoModule) mCurrentModule).mShutterLag : -1;
    }

    public long getShutterToPictureDisplayedTime() {
        return (mCurrentModule instanceof PhotoModule) ?
                ((PhotoModule) mCurrentModule).mShutterToPictureDisplayedTime : -1;
    }

    public long getPictureDisplayedToJpegCallbackTime() {
        return (mCurrentModule instanceof PhotoModule) ?
                ((PhotoModule) mCurrentModule).mPictureDisplayedToJpegCallbackTime : -1;
    }

    public long getJpegCallbackFinishTime() {
        return (mCurrentModule instanceof PhotoModule) ?
                ((PhotoModule) mCurrentModule).mJpegCallbackFinishTime : -1;
    }

    public long getCaptureStartTime() {
        return (mCurrentModule instanceof PhotoModule) ?
                ((PhotoModule) mCurrentModule).mCaptureStartTime : -1;
    }

    public CameraAgent.CameraOpenCallback getCameraOpenErrorCallback() {
        return mCameraController;
    }

    // For debugging purposes only.
    public CameraModule getCurrentModule() {
        return mCurrentModule;
    }

    @Override
    public void finishActivityWithIntentCompleted(Intent resultIntent) {
        finishActivityWithIntentResult(Activity.RESULT_OK, resultIntent);
    }

    @Override
    public void finishActivityWithIntentCanceled() {
        finishActivityWithIntentResult(Activity.RESULT_CANCELED, new Intent());
    }

    private void finishActivityWithIntentResult(int resultCode, Intent resultIntent) {
        mResultCodeForTesting = resultCode;
        mResultDataForTesting = resultIntent;
        setResult(resultCode, resultIntent);
        finish();
    }

    private void keepScreenOnForAWhile() {
        if (mKeepScreenOn) {
            return;
        }
        mMainHandler.removeMessages(MSG_CLEAR_SCREEN_ON_FLAG);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mMainHandler.sendEmptyMessageDelayed(MSG_CLEAR_SCREEN_ON_FLAG, SCREEN_DELAY_MS);
    }

    private void resetScreenOn() {
        mKeepScreenOn = false;
        mMainHandler.removeMessages(MSG_CLEAR_SCREEN_ON_FLAG);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
