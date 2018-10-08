package com.uc.images;

public class ColorMatrixFilter extends ColorMatrixModifier {
    public static final String FILTER_GREY_SCALE="filter.greyScale";
    public static final String FILTER_RED_CHANNEL="filter.redChannel";
    public static final String FILTER_GREEN_CHANNEL="filter.greenChannel";
    public static final String FILTER_BLUE_CHANNEL="filter.blueChannel";
    public static final String FILTER_NEGATIVE="filter.negative";
    public static final String FILTER_NOSTALGIC="filter.nostalgic";
    public static final String FILTER_DECOLORATION="filter.decoloration";
    public static final String FILTER_HIGH_SATURATION="filter.high_saturation";
    public static final String OPTION_GAIN="gain";
    private static final float[] greyArray=new float[]{
        0.299f, 0.587f, 0.114f, 0, 0,
        0.299f, 0.587f, 0.114f, 0, 0,
        0.299f, 0.587f, 0.114f, 0, 0,
             0,      0,      0, 1, 0
    };
    private static final float[] redChannelArray=new float[]{
        1, 0, 0, 0, 0,
        0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,
        0, 0, 0, 1, 0
    };
    private static final float[] greenChannelArray=new float[]{
            0, 0, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 1, 0
    };
    private static final float[] blueChannelArray=new float[]{
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 0, 1, 0
    };
    private static final float[] negativeArray=new float[]{
            -1,  0,  0,  0, 255,
             0, -1,  0,  0, 255,
             0,  0, -1,  0, 255,
             0,  0,  0,  1, 0
    };
    private static final float[] nostalgicArray=new float[]{
            0.393f, 0.769f, 0.189f,  0, 0,
            0.349f, 0.686f, 0.168f,  0, 0,
            0.272f, 0.534f, 0.131f,  0, 0,
                 0,      0,      0,  1, 0
    };
    private static final float[] decolorationArray=new float[]{
            1.5f, 1.5f, 1.5f, 0, -1,
            1.5f, 1.5f, 1.5f, 0, -1,
            1.5f, 1.5f, 1.5f, 0, -1,
               0,    0,    0, 1,  0

    };
    private static final float[] highSaturationArray=new float[]{
             1.438f, -0.122f, -0.016f, 0, -0.03f,
            -0.062f,  1.378f, -0.016f, 0,  0.05f,
            -0.062f, -0.122f,  1.483f, 0, -0.02f,
                  0,       0,       0, 1,      0

    };
    public ColorMatrixFilter(String name, float[] matrix){
        super(name);
        setMatrix(matrix);
    }
    public static class GreyScaleFilter extends ColorMatrixFilter{
        public GreyScaleFilter(){
            super(FILTER_GREY_SCALE, greyArray);
        }
    }
    public static class RedChannelFilter extends ColorMatrixFilter{
        public RedChannelFilter(){
            super(FILTER_RED_CHANNEL, redChannelArray);
        }
    }
    public static class GreenChannelFilter extends ColorMatrixFilter{
        public GreenChannelFilter(){
            super(FILTER_GREEN_CHANNEL, greenChannelArray);
        }
    }
    public static class BlueChannelFilter extends ColorMatrixFilter{
        public BlueChannelFilter(){
            super(FILTER_BLUE_CHANNEL, blueChannelArray);
        }
    }
    public static class NegativeFilter extends ColorMatrixFilter {
        public NegativeFilter(){
            super(FILTER_NEGATIVE, negativeArray);
        }
    }
    public static class NostalgicFilter extends ColorMatrixFilter{
        public NostalgicFilter(){
            super(FILTER_NOSTALGIC, nostalgicArray);
        }
    }
    public static class DecolorationFilter extends ColorMatrixFilter{
        public DecolorationFilter(){
            super(FILTER_DECOLORATION, decolorationArray);
        }
    }
    public static class HighSaturationFilter extends ColorMatrixFilter{
        public HighSaturationFilter(){
            super(FILTER_HIGH_SATURATION, highSaturationArray);
        }
    }
    protected float gain=1;

    protected void setGainInternal(float gain){
        this.gain = gain;
        float[] array=getColorMatrix().getArray();
        for(int i=0; i< array.length; i++){
            array[i] *= gain;
        }
        getColorMatrix().set(array);
    }

    public float getGain() {
        return gain;
    }

    public void setGain(float gain) {
        setGainInternal(gain);
        notify(this,OPTION_GAIN, gain);
    }

    @Override
    public void onSetOption(String name, Object value) {
        if (OPTION_GAIN.equals(name) && (value instanceof Float)) {
            setGain((float)value);
            return;
        }
        super.setOption(name, value);
    }

    @Override
    public boolean isKnownOption(String name) {
        return OPTION_GAIN.equals(name) ? true : super.isKnownOption(name);
    }

    @Override
    public Object getOption(String name) {
        return OPTION_GAIN.equals(name) ? gain : super.getOption(name);
    }
}
