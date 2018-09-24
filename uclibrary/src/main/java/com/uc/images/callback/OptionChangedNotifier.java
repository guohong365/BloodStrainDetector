package com.uc.images.callback;

public interface OptionChangedNotifier {
    void addOnOptionChangeListener(OnOptionChangeListener onOptionChangeListener);
    void notify(Object sender, String name, Object value);
}
