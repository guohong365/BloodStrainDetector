package com.uc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;

import com.uc.images.BrightnessFilter;
import com.uc.images.ColorMatrixFilter;
import com.uc.images.CombinedColorMatrixModifier;
import com.uc.images.ContrastFilter;
import com.uc.images.EditMode;
import com.uc.images.SaturationFilter;
import com.uc.images.callback.OnOptionChangeListener;
import com.uc.images.helper.BitmapHelper;
import com.yalantis.ucrop.util.FastBitmapDrawable;
import com.yalantis.ucrop.view.GestureCropImageView;

public class ImageEditView  extends GestureCropImageView {
    private static final String TAG="ImageEditView";
    private boolean dirty;
    private EditMode mode;
    //截获loadCompleted消息，备份原始图像
    private TransformImageListener outsideListener;
    private Bitmap backup=null;
    private final CombinedColorMatrixModifier tuneModifier = new CombinedColorMatrixModifier();
    private ColorMatrixFilter colorMatrixFilter;
    public ImageEditView(Context context) {
        this(context, null);
    }
    public ImageEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ImageEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        tuneModifier.addModifier(new BrightnessFilter());
        tuneModifier.addModifier(new ContrastFilter());
        tuneModifier.addModifier(new SaturationFilter());
        tuneModifier.addOnOptionChangeListener(tuneOptionChangedListener);

        mTransformImageListener = new TransformImageListener() {
            @Override
            public void onLoadComplete() {
                Log.d(TAG, "onLoadComplete: .......");
                if(outsideListener!=null){
                    outsideListener.onLoadComplete();
                }
            }

            @Override
            public void onLoadFailure(@NonNull Exception e) {
                Log.d(TAG, "onLoadFailure: .......................");
                if(outsideListener!=null){
                    outsideListener.onLoadFailure(e);
                }
            }

            @Override
            public void onRotate(float currentAngle) {
                Log.d(TAG, "onRotate: ......................");
                if(outsideListener!=null){
                    outsideListener.onRotate(currentAngle);
                }
            }

            @Override
            public void onScale(float currentScale) {
                Log.d(TAG, "onScale: ...........................");
                if(outsideListener!=null){
                    outsideListener.onScale(currentScale);
                }
            }
        };
        super.setTransformImageListener(mTransformImageListener);
    }

    private final OnOptionChangeListener tuneOptionChangedListener=new OnOptionChangeListener() {
        @Override
        public void onOptionChanged(Object sender, String key, Object newValue) {
            if(getDrawable()!=null){
                applyTunePreview();
            }
        }
    };

    public void backup(){
        backup= BitmapHelper.dup(getViewBitmap());
    }
    public void restore(){
        setImageMatrix(new Matrix());
        setImageBitmap(backup);
        backup=null;
    }

    public void setColorMatrixFilter(ColorMatrixFilter colorMatrixFilter) {
        if(this.colorMatrixFilter!=colorMatrixFilter){
            this.colorMatrixFilter = colorMatrixFilter;
            applyFilterPreview();
        }
    }

    public ColorMatrixFilter getColorMatrixFilter() {
        return colorMatrixFilter;
    }

    @Override
    public void setTransformImageListener(TransformImageListener transformImageListener) {
        outsideListener=transformImageListener;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public void processStyledAttributes(@NonNull TypedArray a) {
        super.processStyledAttributes(a);
    }

    public void setMode(EditMode mode) {
        this.mode = mode;
    }

    public EditMode getMode() {
        return mode;
    }

    public void applyTunePreview(){
        tuneModifier.applyForPreview((FastBitmapDrawable)getDrawable());
        invalidate();
     }
    public void commitTune(){
        tuneModifier.applyTo((FastBitmapDrawable) getDrawable());
        setDirty(true);
        getDrawable().clearColorFilter();
    }

    public void applyFilterPreview(){
        if(colorMatrixFilter!=null) {
            colorMatrixFilter.applyForPreview((FastBitmapDrawable) getDrawable());
        } else {
            getDrawable().clearColorFilter();
        }
        invalidate();
    }
    public void commitFilter(){
        if(colorMatrixFilter!=null) {
            setDirty(true);
            colorMatrixFilter.applyTo((FastBitmapDrawable) getDrawable());
        }
        getDrawable().clearColorFilter();
    }

    public void commitMarks(){

    }

    public void setBrightness(float brightness) {
        Log.d(TAG, "setBrightness: brightness=" + brightness);
        tuneModifier.setOption(BrightnessFilter.OPTION_BRIGHTNESS, brightness);
    }

    public float getBrightness() {
        return (float)tuneModifier.getOption(BrightnessFilter.OPTION_BRIGHTNESS);
    }

    public void setSaturation(float saturation) {
        Log.d(TAG, "setSaturation: " + saturation);
        tuneModifier.setOption(SaturationFilter.OPTION_SATURATION, saturation);
    }

    public float getSaturation() {
        return (float)tuneModifier.getOption(SaturationFilter.OPTION_SATURATION);
    }

    public void setContrast(float contrast) {
        Log.d(TAG, "setContrast: " + contrast);
        tuneModifier.setOption(ContrastFilter.OPTION_CONTRAST, contrast);
    }

    public float getContrast() {
        return (float)tuneModifier.getOption(ContrastFilter.OPTION_CONTRAST);
    }
}
