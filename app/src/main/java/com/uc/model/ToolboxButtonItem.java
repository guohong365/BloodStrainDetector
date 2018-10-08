package com.uc.model;

public class ToolboxButtonItem extends AbstractSelectable {
    private int iconResId;
    private ToolboxButtonItem parent;
    private ToolboxItem subToolbox;

    public ToolboxButtonItem(){}

    public ToolboxButtonItem(int iconResId){
        this.iconResId=iconResId;
    }

    @Override
    public Long getId() {
        return null;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setParent(ToolboxButtonItem parent) {
        this.parent = parent;
    }

    public ToolboxButtonItem getParent() {
        return parent;
    }

    public void setSubToolbox(ToolboxItem subToolBox) {
        this.subToolbox = subToolBox;
    }

    public ToolboxItem getSubToolbox() {
        return subToolbox;
    }
}
