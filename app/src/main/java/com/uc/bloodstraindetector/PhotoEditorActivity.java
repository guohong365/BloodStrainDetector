package com.uc.bloodstraindetector;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.uc.bloodstraindetector.R;
import com.uc.activity.ActivityBase;
import com.uc.android.camera.app.CameraApp;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageItemUtils;
import com.uc.bloodstraindetector.utils.FileHelper;
import com.uc.images.ColorMatrixFilter;
import com.uc.images.EditMode;
import com.uc.images.callback.OnOptionChangeListener;
import com.uc.task.BitmapSaveTask;
import com.uc.widget.EditModeSelectionLayout;
import com.uc.widget.ImageEditView;
import com.uc.widget.ImageEditViewOverlay;
import com.uc.widget.ImageEditorLayout;
import com.uc.widget.ImageMarkOverlay;
import com.uc.widget.OptionBarLayout;
import com.uc.widget.OptionControlListener;
import com.uc.widget.ProgressWheelView;
import com.uc.widget.RotateProgressWheel;
import com.uc.widget.TuneOptionsLayout;
import com.uc.widget.WrapperProgressWheel;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.view.TransformImageView;

import java.io.File;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PhotoEditorActivity extends ActivityBase
    implements TransformImageView.TransformImageListener {
    public static final String EXTRA_INPUT = "input";
    public static final String EXTRA_OUTPUT = "output";
    public static final String EXTRA_CASE_ID="caseId";
    public static final String EXTRA_PROCESS_ERROR = "EXTRA_PROCESS_ERROR";
    private static final int ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 42;
    private static final int SCALE_WIDGET_SENSITIVITY_COEFFICIENT = 15000;
    private static final float MAX_CONTRACT= 2;
    private static final float MAX_SATURATION=2;

    private ImageEditorLayout imageEditorLayout;
    private RotateProgressWheel layoutRotationWheel;
    private WrapperProgressWheel layoutScaleWheel;
    private OptionBarLayout layoutCropOptions;
    private OptionBarLayout layoutTuneOptions;
    private OptionBarLayout layoutFilterOptions;
    private OptionBarLayout layoutMarkOptions;
    private EditModeSelectionLayout modeSelectionLayout;
    private ImageEditViewOverlay editViewOverlay;
    private ImageEditView imageEditView;
    private ImageMarkOverlay markOverlay;
    private View optionsFrame;
    private View blockingView;
    private boolean showLoader = true;
    private Uri inputUri;
    private Uri outputUri;
    private Long caseId;

    public PhotoEditorActivity() {
    }

    @Override
    protected void onCreateTasks(Bundle savedInstanceState) {
        setContentView(R.layout.activity_photo_editor);
        setupViews();
        setupImageData();
    }

    private void setupViews() {
        setupAppBar();
        initiateViews();
        bindWheel();
        setupCropOptionsControl();
        setupTuneOptionsControl();
        setupMarkOptionsControl();
        setupFilterOptionsControl();
        setupBlockingView();
        updateEditMode(EditMode.View);
    }

    private void setupAppBar() {
        final Toolbar toolbar = findViewById(R.id.tool_bar);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_cancel).mutate();
        toolbar.setNavigationIcon(drawable);
        TextView textView=findViewById(R.id.title_tool_bar);
        textView.setText(R.string.titleImageEditActivity);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }


    protected void setResultError(Throwable throwable) {
        setResult(RESULT_CANCELED, new Intent().putExtra(EXTRA_PROCESS_ERROR, throwable));
    }

    //from TransformImageView.TransformImageListener
    @Override
    public void onLoadComplete() {
        imageEditorLayout.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
        blockingView.setClickable(false);
        showLoader = false;
        supportInvalidateOptionsMenu();
        Log.d(TAG, "onLoadComplete: image loaded.[" + imageEditView.getImageInputPath() + "]");
    }

    @Override
    public void onLoadFailure(@NonNull Exception e) {
        setResultError(e);
        finish();
    }

    @Override
    public void onRotate(float currentAngle) {
        layoutRotationWheel.setLabel(String.format("%.1f°", currentAngle));
    }

    @Override
    public void onScale(float currentScale) {
        if (layoutScaleWheel != null) {
            layoutScaleWheel.setLabel(String.format("%d%%", (int) (currentScale * 100)));
        }
    }


    void updateEditMode(EditMode mode) {
        Log.d(TAG, "setEditMode: ......begin.");
        imageEditorLayout.setMode(mode);
        Log.d(TAG, "setEditMode: view layout was set to mode [" + mode + "]");
        layoutCropOptions.setVisibility(mode == EditMode.Crop ? VISIBLE : GONE);
        layoutScaleWheel.setVisibility(mode == EditMode.Crop ? VISIBLE : GONE);
        layoutRotationWheel.setVisibility(mode == EditMode.Crop ? VISIBLE : GONE);
        layoutFilterOptions.setVisibility(mode == EditMode.Filter ? View.VISIBLE : View.GONE);
        layoutTuneOptions.setVisibility(mode == EditMode.Tune ? View.VISIBLE : View.GONE);
        layoutMarkOptions.setVisibility(mode == EditMode.Mark ? View.VISIBLE : View.GONE);
        modeSelectionLayout.selectMode(mode, false);
        modeSelectionLayout.setVisibility(mode==EditMode.View ? VISIBLE : GONE);
    }

    private void initiateViews() {
        imageEditorLayout = findViewById(R.id.layout_image_editor);
        imageEditView = imageEditorLayout.getImageEditView();
        editViewOverlay = imageEditorLayout.getImageEditViewOverlay();
        layoutRotationWheel = findViewById(R.id.layout_rotate_wheel);
        layoutScaleWheel = findViewById(R.id.layout_scale_wheel);
        layoutCropOptions = findViewById(R.id.layout_crop_options);
        layoutFilterOptions = findViewById(R.id.layout_filter_options);
        layoutTuneOptions = findViewById(R.id.layout_tune_options);
        layoutMarkOptions = findViewById(R.id.layout_mark_options);
        modeSelectionLayout = findViewById(R.id.wrapper_actions);
        markOverlay=findViewById(R.id.image_mark_overlay);
        imageEditView.setTransformImageListener(this);
        modeSelectionLayout.addOnOptionChangeListener(new OnOptionChangeListener() {
            @Override
            public void onOptionChanged(Object sender, String key, Object newValue) {
                EditMode mode = (EditMode) newValue;
                updateEditMode(mode);

            }
        });
    }

    private void setupCropOptionsControl() {
        layoutCropOptions.setOptionControlListener(new OptionControlListener() {
            @Override
            public void onControlOptionChanged(Object sender, String name, Object value) {
                Log.d(TAG, "ratio changed : [" + name + "]=[" + value + "]");
                float ratio = (float) value;
                imageEditorLayout.setTargetAspectRatio(ratio);
            }
            @Override
            public void onCommit(Map<String, Object> Options) {
                applyCrop();
            }

            @Override
            public void onCancel() {
                cancelCrop();
            }
        });
    }

    private void setupTuneOptionsControl() {
        layoutTuneOptions.setOptionControlListener(new OptionControlListener() {
            @Override
            public void onControlOptionChanged(Object sender, String name, Object value) {
                Log.d(TAG, "onOptionChanged: " + name + " changed to: " + value);
                switch (name) {
                    case TuneOptionsLayout.AS_BRIGHTNESS:   //-255 - +255
                        float b= (float)value*510-255;
                        imageEditView.setBrightness(b);
                        break;
                    case TuneOptionsLayout.AS_CONTRAST:  //0 - 2
                        imageEditView.setContrast(((float) value) * 2);
                        break;
                    case TuneOptionsLayout.AS_SATURATION: //0 - 2
                        imageEditView.setSaturation(((float) value) * 2);
                        break;
                }
            }

            @Override
            public void onCommit(Map<String, Object> options) {
                applyTune();
            }

            @Override
            public void onCancel() {
                cancelTune();
            }
        });
    }

    private void setupFilterOptionsControl() {
        layoutFilterOptions.setOptionControlListener(new OptionControlListener() {
            @Override
            public void onControlOptionChanged(Object sender, String name, Object value) {
                imageEditView.setColorMatrixFilter((ColorMatrixFilter) value);
            }

            @Override
            public void onCommit(Map<String, Object> options) {
                applyFilter();
            }

            @Override
            public void onCancel() {
                cancelFilter();
            }
        });
    }

    private void setupMarkOptionsControl() {
        layoutMarkOptions.setOptionControlListener(new OptionControlListener() {
            @Override
            public void onControlOptionChanged(Object sender, String name, Object value) {
                showToast("mark option selected. [" + name + "]=[" + value + "]");
            }

            @Override
            public void onCommit(Map<String, Object> Options) {
                applyMark();
            }

            @Override
            public void onCancel() {
                cancelMark();
            }
        });

    }

    private void bindWheel() {
        layoutScaleWheel.setOnScrollingListener(new ProgressWheelView.OnScrollingListener() {
            @Override
            public void onScrollStart() {
                imageEditView.cancelAllAnimations();
            }

            @Override
            public void onScroll(float delta, float totalDistance, float ticks) {
                imageEditorLayout.zoom(delta / SCALE_WIDGET_SENSITIVITY_COEFFICIENT);
            }

            @Override
            public void onScrollEnd() {
                if (imageEditorLayout.getMode() == EditMode.Crop) {
                    imageEditView.setImageToWrapCropBounds();
                }
            }
        });
        layoutRotationWheel.setOnScrollingListener(new ProgressWheelView.OnScrollingListener() {
            @Override
            public void onScrollStart() {
                imageEditView.cancelAllAnimations();
            }

            @Override
            public void onScroll(float delta, float totalDistance, float ticks) {
                imageEditorLayout.rotate(delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT);
            }

            @Override
            public void onScrollEnd() {
                if (imageEditorLayout.getMode() == EditMode.Crop) {
                    imageEditView.setImageToWrapCropBounds();
                }
            }
        });
        layoutRotationWheel.setRotateProgressWheelListener(new RotateProgressWheel.RotateProgressWheelListener() {
            @Override
            public void onReset() {
                imageEditorLayout.resetRotation();
                Log.d(TAG, "onReset: .....");
            }

            @Override
            public void onRotate90() {
                imageEditorLayout.rotate(90);
                Log.d(TAG, "onRotate90: .....");
            }
        });
    }

    private void setupImageData() {
        Intent intent = getIntent();
        inputUri = intent.getParcelableExtra(EXTRA_INPUT);
        outputUri = intent.getParcelableExtra(EXTRA_OUTPUT);
        caseId=intent.getLongExtra(EXTRA_CASE_ID, -1);
        File file = FileHelper.getTempFile(this, CameraApp.getImageFileName(FileHelper.IMAGE_EXT));
        Uri tempUri = Uri.fromFile(file);
        if (inputUri != null) {
            try {
                imageEditView.setImageUri(inputUri, tempUri);
            } catch (Exception e) {
                setResultError(e);
                finish();
            }
        } else {
            setResultError(new NullPointerException(getString(R.string.ucrop_error_input_data_is_absent)));
            finish();
        }
    }

    private void setupBlockingView() {
        if (blockingView == null) {
            blockingView = new View(this);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            blockingView.setLayoutParams(lp);
            blockingView.setClickable(true);
            ViewGroup layout = findViewById(R.id.wrapper_frame);
            layout.addView(blockingView);
        }
    }

    private void reloadCropResult(Uri uri) {
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
        if (bitmap != null) {
            imageEditView.setImageBitmap(bitmap);
        }
    }


    private void saveImage(Uri uri) {
        blockingView.setClickable(true);
        new BitmapSaveTask(this, imageEditView.getViewBitmap(), uri, null, new BitmapSaveTask.BitmapSaveCallback() {
            @Override
            public void onBitmapSaved(Uri outputUri) {
                blockingView.setClickable(false);
                PhotoEditorActivity.this.showToast("保存成功。");
                Intent intent = new Intent();
                intent.putExtra(EXTRA_OUTPUT, outputUri);
                PhotoEditorActivity.this.setResult(RESULT_OK, intent);
                PhotoEditorActivity.this.finish();
            }

            @Override
            public void onSaveFailure(Throwable throwable) {
                blockingView.setClickable(false);
                PhotoEditorActivity.this.showToast("保存失败。");
            }
        }).execute();
    }

    private void applyCrop() {
        Log.d(TAG, "applyCrop: crop applied......");
        blockingView.setClickable(true);
        showLoader = true;
        supportInvalidateOptionsMenu();
        imageEditView.cropAndSaveImage(Bitmap.CompressFormat.JPEG, 100, new BitmapCropCallback() {
            @Override
            public void onCroppedBitmapSaved(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                Log.d(TAG, "onBitmapCropped: uri=" + resultUri.toString() + " offset x=" + offsetX + " offset y=" + offsetY + " width=" + imageWidth + " height=" + imageHeight);
                try {
                    Log.d(TAG, "onBitmapCropped: to reload croped image.");
                    imageEditView.setDirty(true);
                    imageEditView.setImageUri(resultUri, resultUri);
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("system failure.");
                    finish();
                }
            }

            @Override
            public boolean onBitmapCropped(Bitmap resultBitmap) {
                return false;
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                showToast("crop image failed.....");
                Log.d(TAG, "onCropFailure: crop failed. " + t.toString());
                blockingView.setClickable(false);
                showLoader = false;
                supportInvalidateOptionsMenu();
            }
        });
        updateEditMode(EditMode.View);
    }

    private void cancelCrop() {
        updateEditMode(EditMode.View);
    }

    private void applyFilter() {
        imageEditView.commitFilter();
        layoutFilterOptions.reset();
        updateEditMode(EditMode.View);
    }

    private void cancelFilter() {
        updateEditMode(EditMode.View);
    }

    private void applyTune() {
        imageEditView.commitTune();
        updateEditMode(EditMode.View);
    }

    private void cancelTune() {
        updateEditMode(EditMode.View);
    }

    private void applyMark() {

    }

    private void cancelMark() {
        updateEditMode(EditMode.View);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_photo_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.option_menu_save_as).setVisible(!showLoader);
        menu.findItem(R.id.option_menu_loader).setVisible(showLoader);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.option_menu_save) {
            saveImage(inputUri);
        } else if(item.getItemId()==R.id.option_menu_save_as){
            if(outputUri==null){
                File file=FileHelper.getImageFile(this, CameraApp.getImageFileName(FileHelper.IMAGE_EXT));
                outputUri=Uri.fromFile(file);
            }
            saveImage(outputUri);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStopTasks() {
        super.onStopTasks();
        if (imageEditView != null) {
            imageEditView.cancelAllAnimations();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if (imageEditView.isDirty()) {
                Log.d(TAG, "onKeyDown: image was modified.");
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_title_warning)
                        .setMessage(R.string.dialog_message_if_to_discard_data)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: NO was clicked. ");
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.dialog_no, null).show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ......");

        super.onBackPressed();
    }
}
