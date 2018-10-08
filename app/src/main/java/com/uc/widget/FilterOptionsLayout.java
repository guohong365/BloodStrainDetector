package com.uc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.uc.R;
import com.uc.images.ColorMatrixFilter;
import com.uc.images.callback.OnOptionChangeListener;
import com.uc.images.callback.OptionChangedNotifier;
import com.uc.model.ColorItem;

import java.util.ArrayList;
import java.util.List;

public class FilterOptionsLayout extends LinearLayout implements OptionChangedNotifier{
    private static final String TAG="FilterOptionsLayout";
    public static final String OPTION_FILTER="filter";
    private ColorItemView[] viewGroups;

    private final List<OnOptionChangeListener> listeners=new ArrayList<>();
    public FilterOptionsLayout(Context context) {
        this(context, null);
    }

    public FilterOptionsLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterOptionsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buildFilters();
    }

    private void buildFilters(){
        TypedArray icons=getResources().obtainTypedArray(com.uc.R.array.filter_colors);
        Log.d(TAG, "buildFilters: icon count=" + icons.length());
        ColorMatrixFilter[] filters=new ColorMatrixFilter[]{
                new ColorMatrixFilter.GreyScaleFilter(),
                new ColorMatrixFilter.RedChannelFilter(),
                new ColorMatrixFilter.GreenChannelFilter(),
                new ColorMatrixFilter.BlueChannelFilter(),
                new ColorMatrixFilter.NegativeFilter()
        };
        int[] colorDrawables=new int[]{
                com.uc.R.drawable.ic_filter_gray,
                com.uc.R.drawable.ic_filter_red,
                com.uc.R.drawable.ic_filter_green,
                com.uc.R.drawable.ic_filter_blue,
                com.uc.R.drawable.ic_filter_negative

        };
        Log.d(TAG, "buildFilters: filter count=" + filters.length);
        viewGroups=new ColorItemView[icons.length()];
        int size=getResources().getDimensionPixelSize(R.dimen.sizeColorFilterIcon);
        for(int i=0; i< icons.length(); i++) {
            Log.d(TAG, "buildFilters: item " + i);
            ColorItem colorItem=new ColorItem(0, colorDrawables[i], size,size);
            ColorItemView viewGroup =new ColorItemView(getContext());
            LinearLayout.LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight=1;
            viewGroup.setColorItem(colorItem);
            viewGroup.setTag(filters[i]);
            viewGroup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.isSelected()) return;
                    for(int i=0; i< viewGroups.length; i++) {
                        if (viewGroups[i].isSelected()) viewGroups[i].setSelected(false);
                    }
                    v.setSelected(true);
                    FilterOptionsLayout.this.notify(FilterOptionsLayout.this, OPTION_FILTER, v.getTag());
                }
            });
            viewGroups[i]=viewGroup;
            addView(viewGroup);
        }
        icons.recycle();
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
