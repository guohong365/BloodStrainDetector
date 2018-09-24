package com.uc.images;

import android.graphics.Bitmap;

import com.uc.model.TitledObject;


public interface ImageFilter extends TitledObject {
    String getCategory();
    Bitmap apply(Bitmap input);
    boolean isKnownOption(String name);
    void setOption(String name, Object value);
    Object getOption(String name);
}
