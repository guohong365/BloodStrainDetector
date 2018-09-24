package com.uc.images;

import android.graphics.drawable.Drawable;

import com.yalantis.ucrop.util.FastBitmapDrawable;

public interface ImageModifier extends ImageFilter{
    void applyForPreview(FastBitmapDrawable drawable);
    void applyTo(FastBitmapDrawable drawable);
}
