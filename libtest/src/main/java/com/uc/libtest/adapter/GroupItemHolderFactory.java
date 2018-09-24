package com.uc.libtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uc.libtest.R;
import com.uc.widget.adapter.RecyclerViewLayoutManagerFactory;
import com.uc.widget.adapter.ViewHolderFactoryBase;


public class GroupItemHolderFactory extends ViewHolderFactoryBase<GroupItemHolder> {
    private final RecyclerViewLayoutManagerFactory itemLayoutManagerFactory;
    public GroupItemHolderFactory(Context context, RecyclerViewLayoutManagerFactory itemLayoutManagerFactory) {
        super(context);
        this.itemLayoutManagerFactory=itemLayoutManagerFactory;
    }

    @Override
    public GroupItemHolder create(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        GroupItemHolder holder=new GroupItemHolder(view);
        holder.itemsRecyclerView.setLayoutManager(itemLayoutManagerFactory.create());
        return holder;
    }
}
