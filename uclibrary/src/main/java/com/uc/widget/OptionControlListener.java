package com.uc.widget;

import java.util.Map;

public interface OptionControlListener{
    void onControlOptionChanged(Object sender, String key, Object newValue);
    void onCommit(Map<String, Object> Options);
    void onCancel();
}
