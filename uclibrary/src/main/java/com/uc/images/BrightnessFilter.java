package com.uc.images;

public class BrightnessFilter extends ColorMatrixModifier {
    public static final String FILTER_BRIGHTNESS="filter.brightness";
    public static final String OPTION_BRIGHTNESS="brightness";
    private float brightness; // -255, +255
    private float[] matrixArray=new float[]{
               1, 0, 0, 0, 0,
            // 0  1  2  3  4
               0, 1, 0, 0, 0,
            // 5  6  7  8  9
               0, 0, 1, 0, 0,
            //10 11 12 13 14
               0, 0, 0, 1, 0
            //15 16 17 18 19
    };
    public BrightnessFilter() {
        super( FILTER_BRIGHTNESS);
        setBrightnessInternal(1);
    }

    public void setBrightness(float brightness){
        setBrightnessInternal(brightness);
        notify(this, OPTION_BRIGHTNESS, brightness);
    }

    public float getBrightness() {
        return brightness;
    }

    protected void  setBrightnessInternal(float brightness){
        this.brightness=brightness;
        matrixArray[4] = brightness;
        matrixArray[9] = brightness;
        matrixArray[14]= brightness;
        getColorMatrix().set(matrixArray);
    }

    @Override
    public void onSetOption(String key, Object newValue) {
        if(OPTION_BRIGHTNESS.equals(key) && (newValue instanceof Float)){
            setBrightness((float)newValue);
            return;
        }
        super.setOption(key, newValue);
    }

    @Override
    public boolean isKnownOption(String name) {
        return OPTION_BRIGHTNESS.equals(name) ? true : super.isKnownOption(name);
    }

    @Override
    public Object getOption(String name) {
        if(OPTION_BRIGHTNESS.equals(name)) return brightness;
        return super.getOption(name);
    }
}
