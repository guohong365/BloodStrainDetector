package com.uc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uc.bloodstraindetector.R;

public class WrapperProgressWheel extends LinearLayout {

    public interface LabelCallback{
        String getLabel(WrapperProgressWheel progressWheel, float distance, float total, float ticks);
    }
    LabelCallback labelCallback;
    ProgressWheelView.OnScrollingListener onScrollingListener;

    public void setOnScrollingListener(ProgressWheelView.OnScrollingListener onScrollingListener) {
        this.onScrollingListener = onScrollingListener;
    }

    public void setLabelCallback(LabelCallback labelCallback) {
        this.labelCallback = labelCallback;
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public WrapperProgressWheel(Context context) {
        this(context, null);
    }

    public WrapperProgressWheel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public WrapperProgressWheel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public WrapperProgressWheel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    private TextView label=null;
    private ProgressWheelView wheel=null;
    private  void init(Context context, AttributeSet attrs){
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.WrapperProgressWheel);
        int layout=array.getResourceId(R.styleable.WrapperProgressWheel_wrapperLayoutId, -1);
        if(layout!=-1){
            LayoutInflater.from(context).inflate(layout, this, true);
        }
        int resId=array.getResourceId(R.styleable.WrapperProgressWheel_labelId, -1);
        if(resId >0){
            label=findViewById(resId);
        }
        resId = array.getResourceId(R.styleable.WrapperProgressWheel_progressWheelId, -1);
        if(resId > 0){
            wheel=findViewById(resId);
        }
        if(array.getBoolean(R.styleable.WrapperProgressWheel_withLabel, true)){
            label.setVisibility(VISIBLE);
        } else {
            label.setVisibility(GONE);
        }
        wheel.setClickable(!array.getBoolean(R.styleable.WrapperProgressWheel_readOnly, false));
        wheel.setOnScrollingListener(new ProgressWheelView.OnScrollingListener() {
            @Override
            public void onScrollStart() {
                if(onScrollingListener!=null){
                    onScrollingListener.onScrollStart();
                }
            }
            @Override
            public void onScroll(float delta, float totalDistance, float ticks) {
                if(labelCallback!=null){
                    label.setText(labelCallback.getLabel(WrapperProgressWheel.this, delta, totalDistance, ticks));
                }
                if(onScrollingListener!=null){
                    onScrollingListener.onScroll(delta, totalDistance, ticks);
                }
            }
            @Override
            public void onScrollEnd() {
                if(onScrollingListener!=null){
                    onScrollingListener.onScrollEnd();
                }
            }
        });
    }

    public ProgressWheelView getWheel() {
        return wheel;
    }

    public TextView getLabel() {
        return label;
    }
}
