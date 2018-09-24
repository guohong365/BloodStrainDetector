package com.uc.model;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ToolboxItem extends ToolboxButtonItem {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL=LinearLayout.VERTICAL;
    private final List<ToolboxButtonItem> buttons=new ArrayList<>();
    private int iconWidth;
    private int iconHeight;
    private int orientation;

    public ToolboxItem(int iconResId, int width, int height, int orientation){
        super(iconResId);
        this.iconHeight=height;
        this.iconWidth=width;
        this.orientation=orientation;
    }

    public List<ToolboxButtonItem> getButtons() {
        return buttons;
    }
    public void addButton(ToolboxButtonItem buttonItem){
        buttons.add(buttonItem);
    }
    public void addButton(ToolboxButtonItem buttonItem, int position){
        buttons.add(position, buttonItem);
    }
    @Override
    public Long getId() {
        return null;
    }

    public int getIconHeight() {
        return iconHeight;
    }

    public void setIconHeight(int iconHeight) {
        this.iconHeight = iconHeight;
    }

    public int getIconWidth() {
        return iconWidth;
    }

    public void setIconWidth(int iconWidth) {
        this.iconWidth = iconWidth;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
