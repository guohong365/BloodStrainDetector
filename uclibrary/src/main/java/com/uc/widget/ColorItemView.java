package com.uc.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.uc.R;
import com.uc.model.ColorItem;

public class ColorItemView extends FrameLayout {
    private ColorItem colorItem;

    public ColorItemView(Context context) {
        super(context);
    }

    public ColorItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ColorItem getColorItem() {
        return colorItem;
    }

    public void setColorItem(ColorItem colorItem) {
        this.colorItem = colorItem;
        ImageView view=new ImageView(getContext());
        view.setImageResource(colorItem.getResId());
        LayoutParams lp=new LayoutParams(colorItem.getWidth(), colorItem.getHeight());
        lp.gravity= Gravity.CENTER;
        view.setLayoutParams(lp);
        view.setDuplicateParentStateEnabled(true);
        addView(view);
    }
}
