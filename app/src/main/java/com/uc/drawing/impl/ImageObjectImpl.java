package com.uc.drawing.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.SizeF;

import com.uc.drawing.ImageObject;

public class ImageObjectImpl extends RectangleImpl implements ImageObject {
    Bitmap image;
    Path clip;

    public ImageObjectImpl(PointF position, SizeF size, Bitmap image) {
        super(position, size);
        setImage(image);
    }

    public ImageObjectImpl(PointF position, SizeF size) {
        this(position, size, null);
    }

    @Override
    public Bitmap getImage() {
        return image;
    }

    @Override
    public void setImage(Bitmap image) {
        this.image=image;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getImage()==null) return;
        Paint paint=new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(getImage(),
                new Rect(0,0, getImage().getWidth(), getImage().getHeight()),
                new RectF(0,0, getSize().getWidth(), getSize().getHeight()),
                paint);
    }

    @Override
    public boolean isShowBorder() {
        return  isSelected() || super.isShowBorder();
    }

    @Override
    public void setClip(Path clip) {
        this.clip=clip;
    }
    @Override
    public Path getClip(){
        return clip;
    }
}
