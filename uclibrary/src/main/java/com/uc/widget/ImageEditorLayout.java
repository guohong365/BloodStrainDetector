package com.uc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.uc.R;
import com.uc.images.EditMode;

public class ImageEditorLayout extends FrameLayout  {
    private static final String TAG="ImageEditorLayout";

    public ImageEditorLayout(@NonNull Context context) {
        this(context, null);
    }

    public ImageEditorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageEditorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ImageEditorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private ImageEditView imageEditView;
    private ImageEditViewOverlay imageEditViewOverlay;
    private EditMode mode;

    public void setMode(EditMode mode) {
       this.mode = mode;
       updateViewState();
    }
    public EditMode getMode() {
        return mode;
    }
    void updateViewState(){
        switch (mode){
            case View:
                imageEditViewOverlay.setShowCropRect(false);
                imageEditView.setRotateEnabled(false);
                imageEditView.setScaleEnabled(false);
                Log.d(TAG, "updateViewState: mode=["+mode+"] MODE_VIEW");
                break;
            case Geometry:
                imageEditViewOverlay.setShowCropRect(false);
                imageEditView.setRotateEnabled(true);
                imageEditView.setScaleEnabled(true);
                Log.d(TAG, "updateViewState: mode=["+mode+"] MODE_GEOMETRY");
                break;
            case Crop:
                imageEditViewOverlay.setShowCropRect(true);
                imageEditView.setRotateEnabled(true);
                imageEditView.setScaleEnabled(true);
                Log.d(TAG, "updateViewState: mode=["+mode+"] MODE_CROP");
                break;
            case Filter:
            case Tune:
            case Mark:
                imageEditViewOverlay.setShowCropRect(false);
                imageEditView.setRotateEnabled(false);
                imageEditView.setScaleEnabled(false);
                Log.d(TAG, "updateViewState: mode=["+mode+"] ");
                break;
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImageEditorLayout);
        LayoutInflater.from(context).inflate(R.layout.layout_image_editor, this, true);
        imageEditView = findViewById(R.id.image_edit_view);
        imageEditViewOverlay = findViewById(R.id.image_edit_overlay);
        setListenersToViews();
        array.recycle();
    }


    private void setListenersToViews() {
        imageEditView.setCropBoundsChangeListener(cropRatio -> imageEditViewOverlay.setTargetAspectRatio(cropRatio));
        imageEditViewOverlay.setOverlayViewChangeListener(cropRect -> imageEditView.setCropRect(cropRect));
    }

    public void zoom(float deltaScale){
        float scale=imageEditView.getCurrentScale() + deltaScale*(imageEditView.getMaxScale()-imageEditView.getMinScale());
        if(deltaScale>0){
            imageEditView.zoomInImage(scale);
        } else {
            imageEditView.zoomOutImage(scale);
        }
    }
    public void rotate(float deltaAngle){
        imageEditView.postRotate(deltaAngle);
        imageEditView.setImageToWrapCropBounds();
    }
    public void resetRotation(){
        imageEditView.postRotate(-imageEditView.getCurrentAngle());
        imageEditView.setImageToWrapCropBounds();
    }

    public ImageEditView getImageEditView() {
        return imageEditView;
    }

    public void setTargetAspectRatio(float ratio){
        imageEditView.setTargetAspectRatio(ratio);
        imageEditView.setImageToWrapCropBounds();
    }

    public ImageEditViewOverlay getImageEditViewOverlay() {
        return imageEditViewOverlay;
    }

}