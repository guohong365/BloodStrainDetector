package com.uc.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.uc.bloodstraindetector.R;
import com.yalantis.ucrop.util.SelectedStateListDrawable;

public class RotateProgressWheel extends WrapperProgressWheel {
    public interface RotateProgressWheelListener{
        void onReset();
        void onRotate90();
    }
    private RotateProgressWheelListener rotateProgressWheelListener;

    public void setRotateProgressWheelListener(RotateProgressWheelListener rotateProgressWheelListener) {
        this.rotateProgressWheelListener = rotateProgressWheelListener;
    }

    public RotateProgressWheel(Context context) {
        super(context);
    }

    public RotateProgressWheel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateProgressWheel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotateProgressWheel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Drawable icon=getContext().getDrawable(R.drawable.ic_angle);
        int color=getContext().getColor(R.color.white);
        int activeColor=getContext().getColor(R.color.colorDefaultActiveActionBar);
        ImageView view=findViewById(R.id.action_rotate_90);
        ColorStateList stateList=new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{0}
        }, new int[]{ activeColor, color });
        view.setImageDrawable(new SelectedStateListDrawable(icon, activeColor));
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rotateProgressWheelListener!=null){
                    rotateProgressWheelListener.onRotate90();
                }
            }
        });
        view=findViewById(R.id.action_angle_rest);
        icon = getContext().getDrawable(R.drawable.ic_reset);
        view.setImageDrawable(new SelectedStateListDrawable(icon, activeColor));
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rotateProgressWheelListener!=null){
                    rotateProgressWheelListener.onReset();
                }
            }
        });
    }
}
