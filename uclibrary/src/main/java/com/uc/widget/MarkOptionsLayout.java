package com.uc.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.uc.R;
import com.uc.images.callback.OnOptionChangeListener;
import com.uc.images.callback.OptionChangedNotifier;

import java.util.ArrayList;
import java.util.List;

public class MarkOptionsLayout extends LinearLayout implements OptionChangedNotifier, OnOptionChangeListener {
    private static final String TAG="MarkOptionsLayout";

    public MarkOptionsLayout(Context context) {
        this(context, null);
    }

    public MarkOptionsLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkOptionsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MarkOptionsLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
        @Override
    public void onOptionChanged(Object sender, String key, Object newValue) {
            notify(this,key, newValue);
    }

    private void init(Context context, AttributeSet attrs){
        MarkSelectionLayout markSelectionLayout=findViewById(R.id.layout_mark_selection);
        markSelectionLayout.addOnOptionChangeListener(this);
        ColorSelectionLayout colorSelectionLayout=findViewById(R.id.layout_color_selection);
        colorSelectionLayout.addOnOptionChangeListener(this);
    }

    private List<OnOptionChangeListener> listeners=new ArrayList<>();

    @Override
    public void addOnOptionChangeListener(OnOptionChangeListener onOptionChangeListener) {
        listeners.add(onOptionChangeListener);
    }

    @Override
    public void notify(Object sender, String name, Object value) {
        for(OnOptionChangeListener listener:listeners){
            listener.onOptionChanged(this, name, value);
        }
    }
}
