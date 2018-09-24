package com.uc.bloodstraindetector.view.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uc.bloodstraindetector.R;
import com.uc.widget.adapter.RecyclerViewLayoutManagerFactory;
import com.uc.widget.adapter.ViewHolderFactoryBase;

public class ImageGroupItemHolderFactory extends ViewHolderFactoryBase<ImageGroupItemHolder> {
    private final RecyclerViewLayoutManagerFactory itemLayoutManagerFactory;
    public ImageGroupItemHolderFactory(Context context, RecyclerViewLayoutManagerFactory factory) {
        super(context);
        this.itemLayoutManagerFactory=factory;
    }

    @Override
    public ImageGroupItemHolder create(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.recycler_view_item_case_photo_group, parent, false);
        ImageGroupItemHolder holder=new ImageGroupItemHolder(view);
        holder.itemsRecyclerView.setLayoutManager(itemLayoutManagerFactory.create());
        return holder;
    }
}
