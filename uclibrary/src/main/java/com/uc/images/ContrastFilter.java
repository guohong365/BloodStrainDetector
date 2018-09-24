package com.uc.images;

public class ContrastFilter extends ColorMatrixModifier {
    public static final String FILTER_CONTRAST="filter.contrast";
    public static final String OPTION_CONTRAST="contrast";
    private float contrast; //0 - 10
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

    public ContrastFilter(){
        super(FILTER_CONTRAST);
        setContrastInternal(1);
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        setContrastInternal(contrast);
        notify(this, OPTION_CONTRAST, contrast);
    }

    protected void setContrastInternal(float contrast) {
        this.contrast = contrast;
        matrixArray[0] = contrast;
        matrixArray[6] = contrast;
        matrixArray[12] = contrast;
        matrixArray[4]= 128 *(1-contrast);
        matrixArray[9]= 128 * (1-contrast);
        matrixArray[14] = 128 * (1-contrast);
        getColorMatrix().set(matrixArray);
    }

    @Override
    public boolean isKnownOption(String name) {
        return OPTION_CONTRAST.equals(name) ? true : super.isKnownOption(name);
    }

    @Override
    public void onSetOption(String name, Object newValue) {
        if(OPTION_CONTRAST.equals(name) && (newValue instanceof Float)){
            float scale=(float)newValue;
            setContrast(scale);
            return;
        }
        super.setOption(name, newValue);
    }
}
