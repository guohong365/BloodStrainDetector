package com.uc.images;

public class SaturationFilter extends ColorMatrixModifier {
    public static final String FILTER_SATURATION="filter.saturation";
    public static final String OPTION_SATURATION="saturation";
    private float saturation;
    public SaturationFilter() {
        super(FILTER_SATURATION);
        setSaturationInternal(1);
    }
    protected void setSaturationInternal(float saturation){
        this.saturation=saturation;
        getColorMatrix().setSaturation(saturation);
    }

    public void setSaturation(float saturation) {
        setSaturationInternal(saturation);
        notify(this, OPTION_SATURATION, saturation);
    }

    public float getSaturation() {
        return saturation;
    }

    @Override
    protected void onSetOption(String name, Object value) {
        if (OPTION_SATURATION.equals(name) && (value instanceof Float)) {
            setSaturation((float) value);
            return;
        }
        super.setOption(name, value);
    }

    @Override
    public Object getOption(String name) {
        if(OPTION_SATURATION.equals(name)) return saturation;
        return super.getOption(name);
    }

    @Override
    public boolean isKnownOption(String name) {
        return (OPTION_SATURATION.equals(name) ? true : super.isKnownOption(name));
    }
}
