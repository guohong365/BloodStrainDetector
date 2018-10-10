package com.uc.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.uc.bloodstraindetector.R;
import com.uc.model.TitledObject;

public class OptionLabelView extends TextView {
    private TitledObject optionTag;
    private int activeColor;
    private int unselectedColor;
    private boolean showSelectedIndicator;
    private int indicatorSize;
    private Paint indicatorPaint;
    private Rect clipBounds=new Rect();

    public OptionLabelView(Context context) {
        this(context, null);
    }

    public OptionLabelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionLabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public OptionLabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setGravity(Gravity.CENTER_HORIZONTAL);
        indicatorSize=getContext().getResources().getDimensionPixelSize(R.dimen.sizeOptionLabelSelectedIndicator);
        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint.setStyle(Paint.Style.FILL);
        updateLabel();
        activeColor = getContext().getColor(R.color.colorDefaultActiveOptionItem);
        unselectedColor=getContext().getColor(R.color.colorDefaultOptionLabel);
        applyActiveColor();

    }

    private void applyActiveColor(){
        ColorStateList colorStateList=new ColorStateList(
            new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{0}},
            new int[]{activeColor, unselectedColor});
        setTextColor(colorStateList);
    }
    public void setOptionTag(TitledObject optionTag) {
        this.optionTag = optionTag;
        updateLabel();
    }

    public TitledObject getOptionTag() {
        return optionTag;
    }
    private void updateLabel(){
        if(optionTag!=null){
            setText(optionTag.getTitle());
        }
    }

    public void setTextColor(int activeColor, int unselectedColor) {
        this.activeColor = activeColor;
        this.unselectedColor=unselectedColor;
        applyActiveColor();
    }

    public void setShowSelectedIndicator(boolean showSelectedIndicator) {
        this.showSelectedIndicator = showSelectedIndicator;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected()) {
            canvas.getClipBounds(clipBounds);
            canvas.drawCircle(
                (clipBounds.right - clipBounds.left) / 2.0f,
                clipBounds.bottom - indicatorSize,
                indicatorSize / 2,
                indicatorPaint);
        }
    }
}
