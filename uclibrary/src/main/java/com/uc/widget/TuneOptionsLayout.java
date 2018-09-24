package com.uc.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.uc.R;
import com.uc.images.callback.OnOptionChangeListener;
import com.uc.images.callback.OptionChangedNotifier;
import com.yalantis.ucrop.util.SelectedStateListDrawable;

import java.util.ArrayList;
import java.util.List;

public class TuneOptionsLayout extends LinearLayout implements OptionChangedNotifier, Renewable, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "TuneOptionsLayout";

    public TuneOptionsLayout(Context context) {
        super(context);
    }

    public TuneOptionsLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TuneOptionsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static final String AS_BRIGHTNESS = "brightness";
    public static final String AS_CONTRAST = "contrast";
    public static final String AS_SATURATION = "saturation";
    static final String[] types = {AS_BRIGHTNESS, AS_CONTRAST, AS_SATURATION};
    private int currentType = 0;
    private SeekBar seekBar;
    private ImageView[] buttons = new ImageView[3];
    private static final int[] defaultValues = new int[]{100, 100, 100};
    private List<OnOptionChangeListener> listeners = new ArrayList<>();

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "onProgressChanged: ..........");
        if (fromUser) {
            Log.d(TAG, "onProgressChanged: from user:" + progress);
            float value = (float) progress / seekBar.getMax();
            Log.d(TAG, "onProgressChanged: value = " + value);
            buttons[currentType].setTag(progress);
            notify(this, types[currentType], (float) progress / seekBar.getMax());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < 3; i++) {
                if (buttons[i].getId() == v.getId()) {
                    if (currentType == i) return;
                    currentType = i;
                    buttons[i].setSelected(true);
                    seekBar.setProgress((int) buttons[i].getTag());
                } else {
                    buttons[i].setSelected(false);
                }
            }
        }
    };

    @ColorInt
    int activeColor;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        activeColor = getContext().getResources().getColor(R.color.colorDefaultActiveOptionItem, null);
        seekBar = findViewById(R.id.option_seek_bar);
        seekBar.setMax(200);
        seekBar.setProgress(100);
        seekBar.setOnSeekBarChangeListener(this);

        buttons[0] = findViewById(R.id.btn_brightness);


        buttons[1] = findViewById(R.id.btn_contrast);
        buttons[2] = findViewById(R.id.btn_saturation);
        for (int i = 0; i < 3; i++) {
            buttons[i].setTag(defaultValues[i]);
            buttons[i].setOnClickListener(onClickListener);
            buttons[i].setImageDrawable(new SelectedStateListDrawable(buttons[i].getDrawable(), activeColor));
        }
        currentType = 0;
        buttons[0].setSelected(true);
    }

    @Override
    public void addOnOptionChangeListener(OnOptionChangeListener onOptionChangeListener) {
        listeners.add(onOptionChangeListener);
    }

    @Override
    public void notify(Object sender, String name, Object value) {
        Log.d(TAG, "notify: " + name + "=" + value);
        Log.d(TAG, "notify: listener count=" + listeners.size());
        for (OnOptionChangeListener listener : listeners) {
            listener.onOptionChanged(this, name, value);
        }
    }

    @Override
    public void reset() {
        for (int i = 0; i < 3; i++) {
            buttons[i].setTag(defaultValues[0]);
            buttons[i].setSelected(true);
        }
        currentType = 0;
        seekBar.setProgress(defaultValues[0]);
    }
}
