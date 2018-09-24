package com.uc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.uc.R;
import com.uc.images.callback.OnOptionChangeListener;
import com.uc.images.callback.OptionChangedNotifier;

import java.util.HashMap;
import java.util.Map;

public class OptionBarLayout extends RelativeLayout implements OptionControl, OnOptionChangeListener{
    private static  final String TAG="OptionBarLayout";

    public OptionBarLayout(Context context) {
        this(context, null);
    }

    public OptionBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public OptionBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public OptionBarLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    private ImageView btnApply;
    private ImageView btnCancel;
    private View optionControl;
    private OptionControlListener optionControlListener;
    OnClickListener onClickListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.option_action_apply){
                commit();
            } else if(v.getId()==R.id.option_action_cancel ){
                cancel();
            }
        }
    };

    @Override
    public void setOptionControlListener(OptionControlListener optionControlListener) {
        this.optionControlListener = optionControlListener;
    }

    private boolean init(Context context, AttributeSet attrs){
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.OptionBarLayout);
        LayoutInflater inflater=LayoutInflater.from(getContext());
        int resId = array.getResourceId(R.styleable.OptionBarLayout_layoutId, -1);

        if(resId==-1) {
            inflater.inflate(R.layout.layout_option_bar, this, true);
            btnApply = findViewById(R.id.option_action_apply);
            btnCancel = findViewById(R.id.option_action_cancel);
            int controlLayout=array.getResourceId(R.styleable.OptionBarLayout_controlLayoutId, -1);

            FrameLayout frameLayout=findViewById(R.id.option_control_container);
            Log.d(TAG, "init: frameLayout=" + frameLayout);
            optionControl= inflater.inflate(controlLayout,null);
            frameLayout.addView(optionControl);
        } else {

            inflater.inflate(resId, this, true);
            resId = array.getResourceId(R.styleable.OptionBarLayout_buttonApplyId, -1);
            if (resId != -1) {
                btnApply = findViewById(resId);
            } else {
                btnApply = findViewById(R.id.option_action_apply);
            }
            resId = array.getResourceId(R.styleable.OptionBarLayout_buttonCancelId, -1);
            if (resId != -1) {
                btnCancel = findViewById(resId);
            } else {
                btnCancel = findViewById(R.id.option_action_cancel);
            }
            resId = array.getResourceId(R.styleable.OptionBarLayout_controlContainer, -1);
            FrameLayout frameLayout = null;
            if (resId != -1) {
                frameLayout = findViewById(resId);
            } else {
                frameLayout = findViewById(R.id.option_control_container);
            }
            resId = array.getResourceId(R.styleable.OptionBarLayout_controlLayoutId, -1);
            if (resId != -1 && frameLayout != null) {
                optionControl = inflater.inflate(resId, frameLayout, true).findViewById(resId);
            } else {
                Log.d(TAG, "init: container is null or not defined controlLayoutId.");
                return false;
            }
        }
        if(optionControl instanceof OptionChangedNotifier){
            ((OptionChangedNotifier)optionControl).addOnOptionChangeListener(this);
        }
        boolean show=array.getBoolean(R.styleable.OptionBarLayout_showButtons, true);
        if(btnApply!=null){
                btnApply.setVisibility(show ? VISIBLE : GONE);
                btnApply.setOnClickListener(onClickListener);
        }
        if(btnCancel!=null) {
            btnCancel.setVisibility(show ? VISIBLE : GONE);
            btnCancel.setOnClickListener(onClickListener);
        }
        return true;
    }

    //inner control notify message processor
    @Override
    public void onOptionChanged(Object sender, String key, Object newValue) {
        options.put(key, newValue);
        notifyControlOptionChanged(sender, key, newValue);
    }

    @Override
    public void notifyControlOptionChanged(Object sender, String key, Object value) {
        if(optionControlListener!=null){
            optionControlListener.onControlOptionChanged(sender, key, value);
        }
    }

    private final Map<String, Object> options=new HashMap<>();

    @Override
    public void commit() {
        if(this.optionControlListener!=null) {
            this.optionControlListener.onCommit(options);
        }
    }

    @Override
    public void cancel() {
        if(this.optionControlListener!=null){
            this.optionControlListener.onCancel();
        }
    }

    @Override
    public void reset() {
        if(optionControl instanceof Renewable){
            ((Renewable)optionControl).reset();
        }
    }
}
