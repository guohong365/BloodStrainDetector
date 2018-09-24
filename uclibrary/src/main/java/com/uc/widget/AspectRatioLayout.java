package com.uc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.uc.R;
import com.uc.images.callback.OnOptionChangeListener;
import com.uc.images.callback.OptionChangedNotifier;
import com.uc.model.AspectRatio;
import com.yalantis.ucrop.view.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AspectRatioLayout extends LinearLayout implements OptionChangedNotifier {
    private static final String TAG="AspectRatioLayout";
    private static final String OPTION_RATIO = "crop_ratio";
    private float aspectRatioBase;
    private View lastSelectItem=null;
    public AspectRatioLayout(Context context) {
        this(context, null);
    }

    public AspectRatioLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public AspectRatioLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    List<View> cropAspectRatioViews=new ArrayList<>();

    void init(Context context, AttributeSet attrs){
        Log.d(TAG, "control is creating...");
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout);
        int labelsId= array.getResourceId(R.styleable.AspectRatioLayout_labels, R.array.labels_aspect_ratio);
        String[] labels= getResources().getStringArray(labelsId);
        Log.d(TAG, "init: labels length=["+ labels.length +"] labels=" + Arrays.asList(labels).toString());
        int ratiosId=array.getResourceId(R.styleable.AspectRatioLayout_ratios, R.array.values_aspect_ratio);
        int[] ratios=getResources().getIntArray(ratiosId);
        Log.d(TAG, "init: ratios length=[" + ratios.length + "] ratios=" + Arrays.asList(ratios).toString());
        int iconsId=array.getResourceId(R.styleable.AspectRatioLayout_icons, -1);
        int[] icons=null;
        if(iconsId!=-1){
            icons=getResources().getIntArray(iconsId);
        }
        Log.d(TAG, "init: ratio base=" + aspectRatioBase);
        aspectRatioBase = array.getInt(R.styleable.AspectRatioLayout_ratioBase, -1);
        if(aspectRatioBase==-1){
            throw new RuntimeException("ratio base must be set.");
        }
        int activeColor=array.getColor(R.styleable.AspectRatioLayout_color,getResources().getColor(R.color.colorDefaultTextActionBar, null));
        int color = array.getColor(R.styleable.AspectRatioLayout_activeColor, getResources().getColor(R.color.colorDefaultActiveActionBar, null));
        int style=array.getInteger(R.styleable.AspectRatioLayout_actionItemStyle, ActionItemView.STYLE_BOTH);
        for(int i=0; i< labels.length; i++){
            FrameLayout frameLayout=new FrameLayout(context);
            LayoutParams lp = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.weight = 1;
            frameLayout.setLayoutParams(lp);
            AspectRatioView ratioView=new AspectRatioView(context);
            ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ratioView.setLayoutParams(params);
            ratioView.setGravity(Gravity.CENTER);
            float ratioX=ratios == null ? CropImageView.SOURCE_IMAGE_ASPECT_RATIO : (i < ratios.length ? ratios[i] : CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
            float ratioY=ratios == null ? CropImageView.SOURCE_IMAGE_ASPECT_RATIO : (i < ratios.length ? aspectRatioBase : CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
            int icon= icons==null ? -1 :(i < icons.length ? icons[i] : -1);
            ratioView.setAspectRatio(new AspectRatio(labels[i], ratioX,  ratioY, icon, style));
            ratioView.setColors(activeColor,color);
            ratioView.setDuplicateParentStateEnabled(true);
            frameLayout.addView(ratioView);
            frameLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem(v);
                }
            });
            addView(frameLayout);
            cropAspectRatioViews.add(frameLayout);
        }
        array.recycle();
    }
    private void selectItem(View item) {
        current = ((AspectRatioView) ((ViewGroup) item).getChildAt(0)).getAspectRatio();
        for (View view : cropAspectRatioViews) {
            view.setSelected(view == item);
            if (view == item) lastSelectItem = view;
        }
        notify(this,OPTION_RATIO, current);
    }
    private float current;
    private List<OnOptionChangeListener> listeners=new ArrayList<>();

    @Override
    public void addOnOptionChangeListener(OnOptionChangeListener onOptionChangeListener) {
        listeners.add(onOptionChangeListener);
    }

    @Override
    public void notify(Object sender,String name, Object value) {
        for (OnOptionChangeListener listener:listeners){
            listener.onOptionChanged(this, name, value);
        }
    }
}
