package com.uc.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uc.bloodstraindetector.R;
import com.uc.model.AspectRatio;
import com.yalantis.ucrop.view.CropImageView;

public class AspectRatioView extends ActionItemViewBase {
    private static final String TAG="AspectRatioView";
    private float aspectRatio;
    private float aspectRatioX;
    private float aspectRatioY;

    public AspectRatioView(Context context) {
        super(context);
    }

    public AspectRatioView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public AspectRatioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AspectRatioView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public void setAspectRatio(@NonNull AspectRatio aspectRatio) {
        setActionLabel(aspectRatio.getTitle());
        if(aspectRatio.getIconId() != -1){
            setActionIcon(aspectRatio.getIconId());
        }
        aspectRatioX = aspectRatio.getRatioX();
        aspectRatioY = aspectRatio.getRatioY();
        if (aspectRatioX == CropImageView.SOURCE_IMAGE_ASPECT_RATIO || aspectRatioY == CropImageView.SOURCE_IMAGE_ASPECT_RATIO) {
            this.aspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO;
        } else {
            this.aspectRatio = aspectRatioX / aspectRatioY;
        }
        style=aspectRatio.getStyle();
        applyColor();
        applyStyle();
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void processStyledAttributes(Context context, AttributeSet attrs) {
        TypedArray array=context.obtainStyledAttributes(R.styleable.AspectRatioView);
        int layout=array.getResourceId(R.styleable.AspectRatioView_layoutId, -1);
        style= array.getInteger(R.styleable.AspectRatioView_actionItemStyle, STYLE_BOTH);
        Log.d(TAG, "init: style=" + style);
        activeColor=array.getColor(R.styleable.AspectRatioView_activeColor, getResources().getColor(R.color.colorDefaultActiveActionBar, null));
        color=array.getColor(R.styleable.AspectRatioView_color, getResources().getColor(R.color.colorDefaultTextActionBar, null));
        if(layout!=-1) {
            Log.d(TAG, "init: layout id applied.");
            LayoutInflater.from(context).inflate(layout, this, true);
            actionIcon = findViewById(R.id.action_item_image);
            actionLabel = findViewById(R.id.action_item_label);
        } else {
            Log.d(TAG, "init: layout id was not set. use detail param.");
            int resId = array.getResourceId(R.styleable.AspectRatioView_icon, -1);
            if(resId == -1){
                Log.d(TAG, "init: icon was not set.");
                actionIcon = null;
                style = STYLE_LABEL_ONLY;
            } else {
                Log.d(TAG, "init: icon found. to create icon view");
                setActionIcon(resId);
            }
            String label = array.getString(R.styleable.AspectRatioView_label);
            if(label==null) {
                Log.d(TAG, "init: label not set.");
                actionLabel = null;
                style = STYLE_ICON_ONLY;
            } else {
                Log.d(TAG, "init: label found. label=" + label + ". to create label view.");
                setActionLabel(label);
            }
        }
        aspectRatioX = array.getFloat(R.styleable.AspectRatioView_ratioX, CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        aspectRatioY = array.getFloat(R.styleable.AspectRatioView_ratioY, CropImageView.SOURCE_IMAGE_ASPECT_RATIO);

        if (aspectRatioX == CropImageView.SOURCE_IMAGE_ASPECT_RATIO || aspectRatioY == CropImageView.SOURCE_IMAGE_ASPECT_RATIO) {
            aspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO;
        } else {
            aspectRatio = aspectRatioX / aspectRatioY;
        }
        array.recycle();
        Log.d(TAG, "init: style=" + style + " after create children views");
        applyColor();
        applyStyle();
    }
}
