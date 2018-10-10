package com.uc.drawing.impl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.TextPaint;
import android.util.SizeF;

import com.uc.drawing.Label;

public class LabelImpl extends RectangleImpl implements Label {
    String text;

    public LabelImpl(PointF position, SizeF size, String text) {
        super(position, size);
        setText(text);
    }
    public LabelImpl(PointF position, SizeF size) {
        super(position, size);
        setText("");
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text=text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint=new TextPaint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getAppearance().getColor());
        //PointF start=new PointF();
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(text, 0, 0, paint);
    }
}
