package com.uc.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uc.R;
import com.yalantis.ucrop.util.SelectedStateListDrawable;

public abstract class ActionItemViewBase extends LinearLayout implements StyledAttributesView{
    private static final String TAG="ActionItemViewBase";
    protected ActionItemViewBase(Context context) {
        this(context, null);
    }

    protected ActionItemViewBase(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    protected ActionItemViewBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processStyledAttributes(context,attrs);
    }

    protected ActionItemViewBase(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        processStyledAttributes(context,attrs);
    }

    protected ImageView actionIcon;
    protected TextView actionLabel;
    protected int style = STYLE_BOTH;
    protected int activeColor;
    protected int color;
    protected Drawable icon;
    public static final int STYLE_ICON_ONLY=0;
    public static final int STYLE_LABEL_ONLY=1;
    public static final int STYLE_BOTH=2;

    protected void applyStyle() {
        if (actionLabel != null) {
            actionLabel.setVisibility(style == STYLE_ICON_ONLY ? GONE : VISIBLE);
        }
        if (actionIcon != null) {
            actionIcon.setVisibility(style == STYLE_LABEL_ONLY ? GONE : VISIBLE);
        }
    }
    protected void applyColor(){
        ColorStateList stateList=new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{0}
        }, new int[]{ activeColor, color });
        if(actionLabel!=null){
            actionLabel.setTextColor(stateList);
        }
        if(actionIcon !=null){
            actionIcon.setImageDrawable(new SelectedStateListDrawable(icon, activeColor));
        }
    }
    public void setStyle(int style){
        this.style=style;
        applyStyle();
    }

    public int getStyle() {
        return style;
    }

    public void setActiveColor(int activeColor) {
        this.activeColor = activeColor;
        applyColor();
    }

    public int getActiveColor() {
        return activeColor;
    }

    public void setColors(int activeColor, int color) {
        this.activeColor=activeColor;
        this.color = color;
        applyColor();
    }

    public void setColor(int color) {
        this.color = color;
        applyColor();
    }
    public int getColor() {
        return color;
    }

    protected void setActionLabel(String label){
        if(actionLabel!=null){
            actionLabel.setText(label);
        } else {
            actionLabel = new TextView(getContext());
            actionLabel.setText(label);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity=Gravity.CENTER;
            actionLabel.setLayoutParams(params);
            actionLabel.setDuplicateParentStateEnabled(true);
            addView(actionLabel);
        }
    }
    protected void setActionIcon(int iconRes){
        icon = getResources().getDrawable(iconRes, null);
        if(actionIcon==null){
            actionIcon = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            actionIcon.setLayoutParams(params);
            actionIcon.setDuplicateParentStateEnabled(true);
            addView(actionIcon, 0);
        }
    }
}
