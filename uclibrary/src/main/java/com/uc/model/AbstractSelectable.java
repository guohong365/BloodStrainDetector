package com.uc.model;

public abstract class AbstractSelectable implements Selectable, Selectable.SelectableObserved, Selectable.SelectedObserved {
    protected boolean selected;
    protected boolean selectable;
    protected Object tag;
    private OnSelectedChangedListener onSelectedChangedListener;
    private OnSelectableChangedListener onSelectableChangedListener;

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        if(this.selected!=selected){
            this.selected = selected;
            if(onSelectedChangedListener!=null){
                onSelectedChangedListener.selectedChange(this);
            }
        }
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setSelectable(boolean selectable) {
        if(this.selectable != selectable){
            this.selectable = selectable;
            if(onSelectableChangedListener!=null){
                onSelectableChangedListener.selectableChange(this);
            }
        }
    }

    @Override
    public <T> T getTag() {
        return (T)tag;
    }

    @Override
    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public void setOnSelectedChangedListener(OnSelectedChangedListener listener) {
        this.onSelectedChangedListener = listener;
    }

    @Override
    public void setOnSelectableChangedListener(OnSelectableChangedListener listener) {
        this.onSelectableChangedListener=listener;
    }

    @Override
    public String toString() {
        return "id=[" + getId() + "]\n" +
                "selectable=[" + selectable + "]\n" +
                "selected=[" + selected + "]\n";
    }
}
