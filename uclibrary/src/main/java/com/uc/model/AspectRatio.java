package com.uc.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class AspectRatio {
    @Nullable
    private final String title;
    private final float ratioX;
    private final float ratioY;
    private final int iconId;
    private final int style;

    public AspectRatio(String title, float ratioX, float ratioY, int iconId, int style) {
        this.title=title;
        this.ratioX=ratioX;
        this.ratioY=ratioY;
        this.iconId=iconId;
        this.style=style;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public float getRatioX() {
        return ratioX;
    }

    public float getRatioY() {
        return ratioY;
    }

    public int getIconId() {
        return iconId;
    }

    public int getStyle() {
        return style;
    }
}
