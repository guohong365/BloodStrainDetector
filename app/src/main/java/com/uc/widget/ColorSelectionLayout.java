package com.uc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.uc.bloodstraindetector.R;
import com.uc.images.callback.OnOptionChangeListener;
import com.uc.images.callback.OptionChangedNotifier;
import com.uc.model.ColorItem;

import java.util.ArrayList;
import java.util.List;

public class ColorSelectionLayout extends LinearLayout implements OptionChangedNotifier {
    private static final String TAG="ColorSelectionLayout";
    public static final String OPTION_COLOR="color_selected";
    private OnClickListener colorItemClickListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.isSelected()) return;
            for(int i= 0; i< colorViews.length; i++){
                if(colorViews[i].isSelected()) colorViews[i].setSelected(false);
            }
            ColorItemView view=(ColorItemView)v;
            v.setSelected(true);
            ColorSelectionLayout.this.notify(ColorSelectionLayout.this,OPTION_COLOR, view.getColorItem().getColor());
        }
    };

    private final List<OnOptionChangeListener> listeners=new ArrayList<>();

    public ColorSelectionLayout(Context context) {
        this(context, null);
    }

    public ColorSelectionLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelectionLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buildColorSelections();
    }

    public ColorSelectionLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        buildColorSelections();
    }
    ColorItemView[] colorViews;
    public void buildColorSelections() {
        Log.d(TAG, "build selections....");
        int[] colors=getResources().getIntArray(R.array.mark_colors);
        TypedArray array=getResources().obtainTypedArray(R.array.mark_color_drawables);
        colorViews = new ColorItemView[colors.length];
        int size=getResources().getDimensionPixelSize(R.dimen.sizeOptionItem);
        for(int i=0; i< colors.length; i++){
            ColorItemView itemView=new ColorItemView(getContext());
            LinearLayout.LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight=1;
            itemView.setLayoutParams(lp);
            itemView.setClickable(true);
            itemView.setOnClickListener(colorItemClickListener);
            itemView.setColorItem(new ColorItem(colors[i], array.getResourceId(i, -1), size, size));
            colorViews[i]=itemView;
            addView(itemView);
        }
        Log.d(TAG, "buildColorSelections: views added:" + getChildCount());
        array.recycle();
    }

    @Override
    public void addOnOptionChangeListener(OnOptionChangeListener onOptionChangeListener) {
        listeners.add(onOptionChangeListener);
    }

    @Override
    public void notify(Object sender,String name, Object value) {
        for(OnOptionChangeListener listener:listeners){
            listener.onOptionChanged(this, name, value);
        }
    }
}
