package com.uc.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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
import com.uc.model.ActionItem;
import com.yalantis.ucrop.util.SelectedStateListDrawable;

public class ActionItemView extends ActionItemViewBase {
    private static final String TAG="ActionItemView";
    public ActionItemView(Context context) {
        super(context);
    }

    public ActionItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

    }

    public ActionItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ActionItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void processStyledAttributes(Context context, AttributeSet attrs){
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.ActionItemView);
        int layout=array.getResourceId(R.styleable.ActionItemView_layoutId, -1);
        style= array.getInteger(R.styleable.ActionItemView_actionItemStyle, STYLE_BOTH);
        Log.d(TAG, "init: style=" + style);
        activeColor=array.getColor(R.styleable.ActionItemView_activeColor, getResources().getColor(R.color.colorDefaultActiveActionBar, null));
        color=array.getColor(R.styleable.ActionItemView_color, getResources().getColor(R.color.colorDefaultTextActionBar, null));
        if(layout!=-1) {
            Log.d(TAG, "init: layout id applied.");
            LayoutInflater.from(context).inflate(layout, this, true);
            actionIcon = findViewById(R.id.action_item_image);
            actionLabel = findViewById(R.id.action_item_label);
        } else {
            Log.d(TAG, "init: layout id was not set. use detail param.");
            int resId = array.getResourceId(R.styleable.ActionItemView_icon, -1);
            if(resId == -1){
                Log.d(TAG, "init: icon was not set.");
                actionIcon = null;
                style = STYLE_LABEL_ONLY;
            } else {
                Log.d(TAG, "init: icon found. to create icon view");
                setActionIcon(resId);
            }
            String label = array.getString(R.styleable.ActionItemView_label);
            if(label==null) {
                Log.d(TAG, "init: label not set.");
                actionLabel = null;
                style = STYLE_ICON_ONLY;
            } else {
                Log.d(TAG, "init: label found. label=" + label + ". to create label view.");
                setActionLabel(label);
            }
        }
        array.recycle();
        Log.d(TAG, "init: style=" + style + " after create children views");
        applyColor();
        applyStyle();
        setClickable(true);
    }
}
