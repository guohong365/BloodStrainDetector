package com.uc.model;

public class ActionItem {
    private String label;
    private int icon;
    private int activeColor;
    private int textColor;
    private int style;

    public ActionItem(String label, int icon, int activeColor, int style){
        this.label=label;
        this.icon=icon;
        this.activeColor=activeColor;
        this.style=style;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setActiveColor(int activeColor) {
        this.activeColor = activeColor;
    }

    public int getActiveColor() {
        return activeColor;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getStyle() {
        return style;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void copyFrom(ActionItem actionItem) {
        this.icon=actionItem.getIcon();
        this.label=actionItem.getLabel();
        this.style=actionItem.getStyle();
        this.activeColor=actionItem.getActiveColor();
        this.textColor=actionItem.getTextColor();
    }
}
