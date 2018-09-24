package com.uc.images;

import android.util.Log;

import com.uc.images.callback.OnOptionChangeListener;
import com.uc.images.callback.OptionChangedNotifier;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractImageFilter implements ImageFilter, OptionChangedNotifier {
    private static final String TAG="AbstractImageFilter";
    private final String title;
    private final String category;
    private final List<OnOptionChangeListener> listeners;
    @Override
    public final String getTitle() {
        return title;
    }

    @Override
    public final String getCategory() {
        return category;
    }


    protected AbstractImageFilter(String category, String title){
        this.category=category;
        this.title=title;
        this.listeners=new ArrayList<>();
    }

    @Override
    public void addOnOptionChangeListener(OnOptionChangeListener onOptionChangeListener) {
        listeners.add(onOptionChangeListener);
    }

    @Override
    public void notify(Object sender, String name, Object value){
        for(OnOptionChangeListener listener:listeners){
            listener.onOptionChanged(this, name, getOption(name));
        }
    }

    protected abstract void onSetOption(String name, Object value);


    @Override
    public final void setOption(String name, Object value) {
        Log.d(TAG, "setOption: " + getTitle() +" " + name + "=["+ value +"]");
        if(isKnownOption(name)){
            Log.d(TAG, "setOption: set known option :" + name);
            onSetOption(name, value);
            notify(this, name, value);
        } else {
            Log.d(TAG, "setOption: option ["+name+"] UNKNOWN");
        }
    }
}
