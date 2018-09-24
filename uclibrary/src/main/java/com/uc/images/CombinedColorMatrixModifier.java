package com.uc.images;

import android.graphics.ColorMatrix;
import android.util.Log;

import com.uc.images.callback.OnOptionChangeListener;

import java.util.ArrayList;
import java.util.List;

public class CombinedColorMatrixModifier extends ColorMatrixModifier {
    private static final String TAG="CombinedColorMatrixModifier";
    private static final String FILTER_COMBINED = "filter.combined";
    private final List<ColorMatrixModifier> modifiers;

    private void combineColorMatrix(){
        ColorMatrix matrix=super.getColorMatrix();
        matrix.reset();
        for(ColorMatrixModifier modifier : modifiers){
            matrix.postConcat(modifier.getColorMatrix());
        }
    }

    public CombinedColorMatrixModifier(){
        super(FILTER_COMBINED);
        modifiers=new ArrayList<>();
    }
    public void addModifier(ColorMatrixModifier modifier){
        modifiers.add(modifier); //no listener for combined items
    }

    @Override
    protected void onSetOption(String name, Object value) {
        for(ImageModifier modifier:modifiers){
            modifier.setOption(name, value);
        }
    }

    @Override
    public boolean isKnownOption(String name) {
        if(OPTION_MATRIX.equals(name)) return false;

        for(ImageModifier modifier:modifiers){
            if(modifier.isKnownOption(name)){
                Log.d(TAG, "isKnownOption: [" + name + "] KNOWN.");
                return true;
            }
        }
        Log.d(TAG, "isKnownOption: [" + name + "] UNKNOWN.");
        return false;
    }

    @Override
    public ColorMatrix getColorMatrix() {
        combineColorMatrix();
        return super.getColorMatrix();
    }

    @Override
    public Object getOption(String name) {
        if(OPTION_MATRIX.equals(name)) return getColorMatrix().getArray();
        for(ImageModifier modifier: modifiers){
            Object value=modifier.getOption(name);
            if(value!=null) return value;
        }
        return null;
    }
}
