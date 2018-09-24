package com.uc.model;

public class ColorItem {
    private final int color;
    private final int resId;
    private final int width;
    private final int height;
    public ColorItem(int color, int resId, int width, int height){
        this.color=color;
        this.resId=resId;
        this.width=width;
        this.height=height;
    }
    public int getColor() {
        return color;
    }

    public int getResId() {
        return resId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}


