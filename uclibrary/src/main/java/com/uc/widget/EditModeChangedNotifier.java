package com.uc.widget;

import com.uc.images.EditMode;

public interface EditModeChangedNotifier {
    void addOnEditModeChangeListener(OnEditModeChangeListener onEditModeChangeListener);
    void notifyEditModeChanged(EditMode mode);
}
