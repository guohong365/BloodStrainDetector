package com.uc.widget;

public interface OptionControl extends Renewable{
    void setOptionControlListener(OptionControlListener optionsControlListener);
    void notifyControlOptionChanged(Object sender, String key, Object value);
    void commit();
    void cancel();
}
