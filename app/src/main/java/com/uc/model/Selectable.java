package com.uc.model;

public interface Selectable extends WithId, WithTag {
    interface OnSelectableChangedListener{
        void selectableChange(Selectable sender);
    }
    interface OnSelectedChangedListener{
        void selectedChange(Selectable sender);
    }

    interface SelectableObserved {
        void setOnSelectableChangedListener(OnSelectableChangedListener listener);
    }
    interface SelectedObserved {
        void setOnSelectedChangedListener(OnSelectedChangedListener listener);
    }

    boolean isSelectable();
    void setSelectable(boolean selectable);
    boolean isSelected();
    void setSelected(boolean selected);

}
