package com.uc.android.camera.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.uc.bloodstraindetector.R;

public class Ruler extends View
    implements PreviewStatusListener.PreviewAreaChangedListener{
    private static final String TAG="Ruler";
    private static final float FULL_SCREEN_RATIO=0.787f;
    private float ratio=FULL_SCREEN_RATIO ;
    @Override
    public void onPreviewAreaChanged(final RectF previewArea) {
        setBounds(previewArea);
    }

    public enum RulerStyle {
        Unkonw(-1),
        None(0),
        Brief(1),
        Detail(2);

        public int getStyleCode() {
            return styleCode;
        }
        public static RulerStyle getByCode(int code) {
            switch (code){
                case 0:
                    return None;
                case 1:
                    return Brief;
                case 2:
                    return Detail;
                default:
                    return Unkonw;
            }
        }
        private final int styleCode;

        RulerStyle(int style) {
            styleCode = style;
        }
    }
    private final float briefScaleSpace;
    private final float detailScaleSpace;
    private final float shortLineLength;
    private final float markLineLength;
    private final int markInterval;
    private final float markNumberDistanceOffset;
    private final float markNumberVerticalOffset;
    private RulerStyle rulerStyle=RulerStyle.None;
    private final Paint paint=new Paint();
    private final Paint markPaint=new Paint();
    private final TextPaint textPaint=new TextPaint();
    private float offset;
    private RectF bounds;
    public Ruler(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.ruler_line_width));
        paint.setColor(getResources().getColor(R.color.ruler_line, null));
        offset=getResources().getDimensionPixelSize(R.dimen.grid_border_width);
        briefScaleSpace=getResources().getDimension(R.dimen.ruler_breif_scale_space);
        detailScaleSpace=getResources().getDimension(R.dimen.ruler_detail_scale_space);
        shortLineLength=getResources().getDimension(R.dimen.ruler_short_line_length);
        markLineLength=getResources().getDimension(R.dimen.ruler_mark_line_length);
        markNumberDistanceOffset=getResources().getDimension(R.dimen.ruler_mark_number_offset) + markLineLength;
        markInterval=getResources().getInteger(R.integer.ruler_mark_interval);
        Log.d(TAG, "Ruler: mark Interval=" + markInterval);
        markPaint.setColor(getResources().getColor(R.color.ruler_mark, null));
        markPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.ruler_mark_line_width));
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getResources().getColor(R.color.ruler_mark, null));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.ruler_mark_number_size));
        Paint.FontMetrics fontMetrics=textPaint.getFontMetrics();
        markNumberVerticalOffset=-(fontMetrics.bottom+fontMetrics.top)/2;
    }

    public RulerStyle getRulerStyle() {
        return rulerStyle;
    }

    public void setRulerStyle(RulerStyle rulerStyle) {
        this.rulerStyle = rulerStyle;
        Log.d(TAG, "setRulerStyle: " + rulerStyle);
        invalidate();
    }

    public void setBounds(RectF bounds) {
        this.bounds = new RectF(bounds);
        Log.d(TAG, "setBounds: (" + bounds.left + "," + bounds.top + "," + bounds.right + "," + bounds.bottom + ")");
        invalidate();
    }

    private float getSpace(){
        return rulerStyle== RulerStyle.Brief ? briefScaleSpace : detailScaleSpace;
    }

    private void drawHorizontal(Canvas canvas, float x, float y, float width, float space, int count, boolean inverse){
        float pos= x;
        for(int i=0; i<count && pos< width; i++){
            if((i!=0) && ((i+1)%markInterval)==0){
                if(inverse) {
                    canvas.drawLine(x, y, x, y -markLineLength, markPaint);
                    canvas.drawText("" + ((i +1) / markInterval), x, y - markNumberDistanceOffset - markNumberVerticalOffset , textPaint);
                } else {
                    canvas.drawLine(x, y, x, y +  markLineLength, markPaint);
                    canvas.drawText("" + ((i+1) / markInterval), x, y + markNumberDistanceOffset - markNumberVerticalOffset , textPaint);
                }
            } else {
                canvas.drawLine(x, y, x, y + (inverse ? -shortLineLength : shortLineLength), paint);
            }
            x += space;
            pos =x;
        }
    }

    private void drawVertical(Canvas canvas, float x, float y, float height, float space, int count, boolean inverse){
        float posY= height ;
        for(int i=0; i< count && posY > y; i++ ){
            if((i !=0) && ((i +1) % markInterval)==0){
                canvas.drawLine( x, posY, x + (inverse? - markLineLength:markLineLength), posY, markPaint);
                canvas.drawText(""+((i +1)/markInterval),
                        x +(inverse ? - markNumberDistanceOffset : markNumberDistanceOffset) ,
                        posY + markNumberVerticalOffset, textPaint);
            } else {
                canvas.drawLine( x , posY, x + (inverse? - shortLineLength:shortLineLength), posY, paint);
            }
            posY-=space;
        }
    }

    private void drawRuler(Canvas canvas, RectF bounds){
        float space=getSpace();
        int count =(int) ((bounds.width() - 2*offset)/space);
        float realHeight=bounds.height(); //bounds.width()*ratio;
        //drawHorizontal(canvas, bounds.left + offset + space, bounds.top + offset, bounds.width() - 2*offset, space, count, false);
        drawHorizontal(canvas, bounds.left + offset + space, bounds.top + realHeight - offset, bounds.width() - 2*offset, space, count, true);

        count=(int)((realHeight - 2*offset)/space);
        drawVertical(canvas, bounds.left + offset, bounds.top + offset + space, realHeight - offset, space, count, false);
        drawVertical(canvas, bounds.right - offset, bounds.top + offset + space, realHeight - offset, space, count, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(bounds!=null && rulerStyle!= RulerStyle.None){
            drawRuler(canvas, bounds);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout: (" + left + "," + top + "," + right + "," + bottom + ")");
        super.onLayout(changed, left, top, right, bottom);
    }
}
