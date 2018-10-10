package com.uc.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.uc.model.ToolboxButtonItem;

public class ToolboxButton extends FrameLayout{
    private ToolboxButtonItem buttonItem;
    public ToolboxButton(@NonNull Context context, ToolboxButtonItem buttonItem) {
        this(context, buttonItem, null);
    }

    public ToolboxButton(@NonNull Context context, ToolboxButtonItem buttonItem, @Nullable AttributeSet attrs) {
        this(context,buttonItem, attrs, 0);
    }

    public ToolboxButton(@NonNull Context context, ToolboxButtonItem buttonItem, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.buttonItem=buttonItem;
        init();
    }
    private void init(){
        if(buttonItem.getSubToolbox()==null) {
            ImageView view = new ImageView(getContext());
            view.setImageResource(buttonItem.getIconResId());
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(layoutParams);
            view.setDuplicateParentStateEnabled(true);
            addView(view);
        } else {
            //ToolBoxLayout layout=new ToolBoxLayout(getContext(), buttonItem.getSubToolBox());
            //TODO: impl sub toolbox
        }
    }
}
