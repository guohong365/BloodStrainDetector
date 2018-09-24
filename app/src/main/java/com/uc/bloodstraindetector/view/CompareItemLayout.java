package com.uc.bloodstraindetector.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.uc.bloodstraindetector.R;
import com.yalantis.ucrop.view.GestureCropImageView;

/**
 * Created by guohong on 2018/1/15.
 */

public class CompareItemLayout extends FrameLayout {
    private static final String TAG="CompareItemLayout";
    private float imageAlpha=1.0f;
    private GestureCropImageView photoView;


    public CompareItemLayout(Context context) {
        super(context);
    }
    public CompareItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompareItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CompareItemLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setImage(Bitmap image) {
        photoView.setImageBitmap(image);
    }

    public float getImageAlpha() {
        return imageAlpha;
    }

    public void setImageAlpha(float imageAlpha) {
        this.imageAlpha=imageAlpha;
        if(photoView!=null){
            Log.d(TAG, String.format("set image alpha %f ", imageAlpha));
            photoView.setImageAlpha((int)(imageAlpha * 255 + 0.5));
        }
    }

    public GestureCropImageView getPhotoView() {
        return photoView;
    }

    @Override
    protected void onFinishInflate() {
        Log.d(TAG, "onFinishInflate");
        super.onFinishInflate();
        photoView=findViewById(R.id.compare_item_image);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout ("+ left +"," + top + "," + right + "," + bottom +")");
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure ("+ MeasureSpec.getSize(widthMeasureSpec) +","+ MeasureSpec.getSize(heightMeasureSpec) +")");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
