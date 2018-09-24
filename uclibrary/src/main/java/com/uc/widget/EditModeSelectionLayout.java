package com.uc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.uc.R;
import com.uc.images.callback.OnOptionChangeListener;
import com.uc.images.callback.OptionChangedNotifier;
import com.uc.images.EditMode;

import java.util.ArrayList;
import java.util.List;


public class EditModeSelectionLayout extends LinearLayout implements OptionChangedNotifier {
    private final List<OnOptionChangeListener> listeners=new ArrayList<>();
    public static final String OPTION_EDIT_MODE="edit_mode";

    public EditModeSelectionLayout(Context context) {
        this(context, null);
    }

    public EditModeSelectionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditModeSelectionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    public EditModeSelectionLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private ViewGroup modeCrop;
    private ViewGroup modeFilter;
    private ViewGroup modeTune;
    private ViewGroup modeMark;

    private void selectModeActionView(int id){
        modeCrop.setSelected(id==modeCrop.getId());
        modeFilter.setSelected(id==modeFilter.getId());
        modeTune.setSelected(id==modeTune.getId());
        modeMark.setSelected(id==modeMark.getId());
    }
    public void selectMode(EditMode mode, boolean notify){
        modeCrop.setSelected(EditMode.Crop == mode);
        modeFilter.setSelected(EditMode.Filter==mode);
        modeTune.setSelected(EditMode.Tune==mode);
        modeMark.setSelected(EditMode.Mark==mode);
        if(notify) notify(this,OPTION_EDIT_MODE, mode);
    }
    OnClickListener itemClickListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            selectModeActionView(v.getId());
            EditModeSelectionLayout.this.notify(EditModeSelectionLayout.this,OPTION_EDIT_MODE, v.getTag());
        }
    };
    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.layout_mode_selection_box, this, true);
        modeCrop=findViewById(R.id.layout_action_geometry);
        modeCrop.setTag(EditMode.Crop);
        modeFilter=findViewById(R.id.layout_action_filter);
        modeFilter.setTag(EditMode.Filter);
        modeTune=findViewById(R.id.layout_action_tune);
        modeTune.setTag(EditMode.Tune);
        modeMark=findViewById(R.id.layout_action_mark);
        modeMark.setTag(EditMode.Mark);

        modeCrop.setOnClickListener(itemClickListener);
        modeFilter.setOnClickListener(itemClickListener);
        modeTune.setOnClickListener(itemClickListener);
        modeMark.setOnClickListener(itemClickListener);

    }

    @Override
    public void addOnOptionChangeListener(OnOptionChangeListener onOptionChangeListener) {
        listeners.add(onOptionChangeListener);
    }

    @Override
    public void notify(Object sender, String name, Object value) {
        for(OnOptionChangeListener listener:listeners){
            listener.onOptionChanged(this, name, value);
        }
    }

    public EditMode getCurrentMode(){
        return modeCrop.isSelected() ? EditMode.Crop :
                (modeFilter.isSelected() ? EditMode.Filter :
                        (modeTune.isSelected() ? EditMode.Tune :
                                (modeMark.isSelected() ? EditMode.Mark : EditMode.View)));
    }
}
