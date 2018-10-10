package com.uc.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.uc.bloodstraindetector.R;
import com.uc.model.MarkItem;

public class MarkItemView extends FrameLayout{
    private MarkItem markItem;
    private int markColor;
    private ImageView markView;
    public MarkItemView(@NonNull Context context) {
        super(context);
    }

    public MarkItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MarkItem getMarkItem() {
        return markItem;
    }

    public void setMarkItem(MarkItem markItem) {
        this.markItem = markItem;
        markView =new ImageView(getContext());
        int size=getResources().getDimensionPixelSize(R.dimen.sizeOptionItem);
        LayoutParams lp=new LayoutParams(size, size);
        lp.gravity= Gravity.CENTER;
        markView.setDuplicateParentStateEnabled(true);
        markView.setImageResource(markItem.getShapeRes());
        addView(markView);
        invalidate();
    }

    public int getMarkColor() {
        return markColor;
    }

    public void setMarkColor(int markColor) {
        this.markColor = markColor;
        markView.setColorFilter(markColor, PorterDuff.Mode.SRC_ATOP);
    }
}
