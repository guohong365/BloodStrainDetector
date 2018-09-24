package com.uc.bloodstraindetector.view;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.uc.bloodstraindetector.R;
import com.uc.bloodstraindetector.model.CompareParams;
import com.yalantis.ucrop.view.TransformImageView;


/**
 * Created by guohog on 2018/2/7.
 */

public class CompareFrameLayout extends FrameLayout {
    private static final String TAG="CompareFrameLayout";
    public static final int SWAP_LEFT_TO_RIGHT=0;
    public static final int SWAP_UP_TO_DOWN=1;
    private CompareParams compareParams;
    private CompareItemLayout firstItem;
    private CompareItemLayout secondItem;
    public CompareFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public CompareFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompareFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater inflater=LayoutInflater.from(getContext());
        firstItem = (CompareItemLayout) inflater.inflate(R.layout.item_compare, null);
        addView(firstItem);
        firstItem.getPhotoView().setTransformImageListener(new TransformImageView.TransformImageListener() {
            @Override
            public void onLoadComplete() {
                Log.d(TAG, "onLoadComplete: first loaded.");
                if(transformImageListener!=null){
                    transformImageListener.onLoadComplete();
                }
            }

            @Override
            public void onLoadFailure(@NonNull Exception e) {
                if(transformImageListener!=null){
                    transformImageListener.onLoadFailure(e);
                }
            }

            @Override
            public void onRotate(float currentAngle) {

            }

            @Override
            public void onScale(float currentScale) {

            }
        });
        secondItem= (CompareItemLayout) inflater.inflate(R.layout.item_compare, null);
        addView(secondItem);
        secondItem.getPhotoView().setTransformImageListener(new TransformImageView.TransformImageListener() {
            @Override
            public void onLoadComplete() {
                Log.d(TAG, "onLoadComplete: second loaded.");
                if(transformImageListener!=null){
                    transformImageListener.onLoadComplete();
                }
            }

            @Override
            public void onLoadFailure(@NonNull Exception e) {

            }

            @Override
            public void onRotate(float currentAngle) {

            }

            @Override
            public void onScale(float currentScale) {

            }
        });
    }

    public CompareItemLayout getFirstItem() {
        return firstItem;
    }

    public CompareItemLayout getSecondItem() {
        return secondItem;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        Log.d(TAG, String.format("onMeasure (width=%d, height=%d)", MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize( heightMeasureSpec)));
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        if(compareParams!=null && compareParams.compareMode== CompareParams.COMPARE_MODE_PARALLEL) {
            firstItem.measure(MeasureSpec.makeMeasureSpec(width / 2, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            secondItem.measure(MeasureSpec.makeMeasureSpec(width / 2, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else {
            firstItem.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            secondItem.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
        Log.d(TAG, String.format("frame rect (%d, %d, %d, %d)",getLeft(), getTop(), getWidth(), getHeight()));
        Log.d(TAG, String.format("onMeasure first=[%d, %d]", firstItem.getMeasuredWidth(), firstItem.getMeasuredHeight()));
        Log.d(TAG, String.format("onMeasure second=[%d, %d]", secondItem.getMeasuredWidth(), secondItem.getMeasuredHeight()));

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, String.format("onLayout input(left=%d, top=%d, right=%d, bottom=%d)", left, top, right, bottom));
        if(compareParams==null) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        Log.d(TAG, String.format("first=[%s] second=[%s] mode=[%d]", compareParams.images[0], compareParams.images[1], compareParams.compareMode));
        if(compareParams.compareMode==CompareParams.COMPARE_MODE_PARALLEL) {
            firstItem.layout(0, 0, firstItem.getMeasuredWidth(), getMeasuredHeight());
            secondItem.layout(firstItem.getMeasuredWidth(), 0, getMeasuredWidth(), getMeasuredHeight());
        } else {
            firstItem.layout(0,0, getMeasuredWidth(), getMeasuredHeight());
            secondItem.layout(0,0,getMeasuredWidth(), getMeasuredHeight());
        }
    }

    public void setCompareParams(CompareParams compareParams) {
        this.compareParams = compareParams;
        try {
            Uri uri = Uri.parse(compareParams.images[0]);
            firstItem.getPhotoView().setImageUri(uri, uri);
            uri = Uri.parse(compareParams.images[1]);
            secondItem.getPhotoView().setImageUri(uri, uri);
            secondItem.setImageAlpha(0.5f);
        } catch (Exception e){
            Log.d(TAG, "setCompareParams: " + e.toString());
            throw new IllegalArgumentException(e);
        }
        requestLayout();
    }

    public void setImageAlpha(float alpha){
        secondItem.setImageAlpha(alpha);
    }
    public CompareParams getCompareParams() {
        return compareParams;
    }

    public void swapImage(int swapType){
        CompareItemLayout layout=firstItem;
        firstItem=secondItem;
        secondItem=layout;
        switch (swapType){
            case SWAP_LEFT_TO_RIGHT:
                break;
            case SWAP_UP_TO_DOWN:
                float alpha=firstItem.getImageAlpha();
                firstItem.setImageAlpha(1);
                firstItem.setElevation(0);;
                secondItem.setImageAlpha(alpha);
                secondItem.setElevation(10);
                break;
        }
        requestLayout();
    }

    private TransformImageView.TransformImageListener transformImageListener;
    public void setTransformImageListener(TransformImageView.TransformImageListener transformImageListener) {
        this.transformImageListener=transformImageListener;
    }

    public void setImageUri(int index, Uri photoUri){
        CompareItemLayout itemLayout= index==0 ? firstItem : secondItem;
        try {
            itemLayout.getPhotoView().setImageUri(photoUri, photoUri);
        } catch (Exception e){
            throw new IllegalArgumentException(e);
        }
    }
}
