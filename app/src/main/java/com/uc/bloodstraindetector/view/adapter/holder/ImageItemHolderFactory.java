package com.uc.bloodstraindetector.view.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uc.bloodstraindetector.R;
import com.uc.utils.ScreenUtils;
import com.uc.widget.adapter.ViewHolderFactoryBase;

public class ImageItemHolderFactory extends ViewHolderFactoryBase<ImageItemHolder> {
    public final int columns;
    private final int contextMenu;
    public ImageItemHolderFactory(Context context,int contextMenu, int columns) {
        super(context);
        this.columns=columns;
        this.contextMenu=contextMenu;
    }

    @Override
    public ImageItemHolder create(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.recycler_view_item_case_photo_item, parent, false);
        int width = ScreenUtils.getScreenWidth(context)/columns;
        view.setLayoutParams(new ViewGroup.LayoutParams(width, width));
        return new ImageItemHolder(view, contextMenu);
    }
}
