package com.uc.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.uc.R;
import com.uc.images.callback.OnOptionChangeListener;
import com.uc.images.callback.OptionChangedNotifier;
import com.uc.model.MarkItem;

import java.util.ArrayList;
import java.util.List;

public class MarkSelectionLayout extends LinearLayout implements OptionChangedNotifier{
    public static final String OPTION_MARK_SELECTED ="mark_selected";
    public static final int MARK_SQUARE=0;
    public static final int MARK_CIRCLE=1;
    public static final int MARK_TRIANGLE=2;
    public static final int MARK_ARROW_LEFT=3;
    public static final int MARK_ARROW_RIGHT=4;
    public static final int MARK_ARROW_DOWN=5;
    public static final int MARK_ARROW_UP=6;

    private int currentMark;
    private List<MarkItemView> markItemViewList=new ArrayList<>();
    private final List<OnOptionChangeListener> listeners=new ArrayList<>();

    OnClickListener onMarkClicked=new OnClickListener() {
        @Override
        public void onClick(View v) {
            MarkItemView  view= (MarkItemView) v;
            currentMark=view.getMarkItem().getId();
            MarkSelectionLayout.this.notify(MarkSelectionLayout.this, OPTION_MARK_SELECTED, currentMark);
        }
    };



    public MarkSelectionLayout(Context context) {
        this(context, null);
    }

    public MarkSelectionLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        buildMarks();
    }

    public MarkSelectionLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buildMarks();
    }

    public MarkSelectionLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        buildMarks();
    }

    protected void buildMarks(){
        List<MarkItem> markItems=new ArrayList<>();
        markItems.add(new MarkItem(MARK_SQUARE, com.uc.R.drawable.ic_mark_square));
        markItems.add(new MarkItem(MARK_CIRCLE, com.uc.R.drawable.ic_mark_circle));
        markItems.add(new MarkItem(MARK_TRIANGLE, com.uc.R.drawable.ic_mark_triangle));
        markItems.add(new MarkItem(MARK_ARROW_LEFT, com.uc.R.drawable.ic_mark_arrow_left));
        markItems.add(new MarkItem(MARK_ARROW_RIGHT, com.uc.R.drawable.ic_mark_arrow_right));
        markItems.add(new MarkItem(MARK_ARROW_DOWN, com.uc.R.drawable.ic_mark_arrow_down));
        markItems.add(new MarkItem(MARK_ARROW_UP, com.uc.R.drawable.ic_mark_arrow_up));

        LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.weight=1;
        for(MarkItem item:markItems){
            MarkItemView itemView= new MarkItemView(getContext());
            itemView.setMarkItem(item);
            itemView.setLayoutParams(lp);
            itemView.setClickable(true);
            itemView.setOnClickListener(onMarkClicked);
            addView(itemView);
            markItemViewList.add(itemView);
        }
    }

    @Override
    public void addOnOptionChangeListener(OnOptionChangeListener onOptionChangeListener) {
        listeners.add(onOptionChangeListener);
    }

    @Override
    public void notify(Object sender,String name, Object value) {
        for(OnOptionChangeListener listener:listeners){
            listener.onOptionChanged(this, name, value);
        }
    }
}
