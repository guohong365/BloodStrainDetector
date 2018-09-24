package com.uc.libtest.adapter;

import android.content.Context;

import com.uc.model.DataGroup;
import com.uc.model.Selectable;
import com.uc.widget.adapter.LinearRecyclerViewLayoutManagerFactory;
import com.uc.widget.adapter.RecyclerViewAdapterBase;
import com.uc.widget.adapter.RecyclerViewDataGroupAdapter;

import java.util.List;

import com.uc.libtest.model.DataItem;

public class GroupAdapter extends RecyclerViewDataGroupAdapter<GroupItemHolder, DataItemHolder> {
    public GroupAdapter(Context context, List<Selectable> groupItems) {
        super(context, groupItems,
                new GroupItemHolderFactory(context, new LinearRecyclerViewLayoutManagerFactory(context)),
                new DataItemHolderFactory(context));
    }

    @Override
    protected RecyclerViewAdapterBase<DataItemHolder> createItemAdapter(Context context, List<Selectable> items) {
        return new ItemAdapter(context, items);
    }

    @Override
    public DataGroup findGroupBySubItem(Selectable subItem) {
        DataItem item= (DataItem) subItem;
        return findGroupByName("GROUP " + item.getGroupId());
    }
}
