package com.uc.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uc.model.ToolboxButtonItem;
import com.uc.model.ToolboxItem;
import com.uc.widget.callback.OnToolboxButtonClickListener;

public class ToolboxLayout extends LinearLayout {
    private ToolboxItem toolBoxItem;
    public ToolboxLayout(Context context) {
        this(context, null);
    }

    public ToolboxLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolboxLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        setBackground(null);
    }

    public void setToolBoxItem(ToolboxItem toolBoxItem){
        this.toolBoxItem=toolBoxItem;
        //leading button
        ImageView view=new ImageView(getContext());
        view.setImageResource(toolBoxItem.getIconResId());
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams layoutParams=new LayoutParams(toolBoxItem.getIconWidth(), toolBoxItem.getIconHeight());
        view.setLayoutParams(layoutParams);
        view.setVisibility(VISIBLE);
        toolBoxItem.setTag(view);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    expand(false);
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                    expand(true);
                }
            }
        });
        addView(view);

        for(ToolboxButtonItem item: toolBoxItem.getButtons()){
            view=new ImageView(getContext());
            view.setImageResource(item.getIconResId());
            layoutParams=new LayoutParams(toolBoxItem.getIconWidth(), toolBoxItem.getIconHeight());
            view.setLayoutParams(layoutParams);
            view.setVisibility(GONE);
            item.setTag(view);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    View root=toolBoxItem.getTag();
                    root.setSelected(false);
                    expand(false);
                    if(onToolboxButtonClickListener!=null){
                        onToolboxButtonClickListener.onClick((ToolboxButtonItem) v.getTag());
                    }
                }
            });
            addView(view);
        }
    }

    //@param expand  TRUE to expand toolbox, FALSE to collapse it.
    protected void expand(boolean expand){
        for(ToolboxButtonItem item : toolBoxItem.getButtons()){
            View view=item.getTag();
            view.setVisibility(expand ? VISIBLE : GONE);
        }
    }

    private OnToolboxButtonClickListener onToolboxButtonClickListener;

    public void setOnToolboxButtonClickListener(OnToolboxButtonClickListener onToolboxButtonClickListener) {
        this.onToolboxButtonClickListener = onToolboxButtonClickListener;
    }
}
