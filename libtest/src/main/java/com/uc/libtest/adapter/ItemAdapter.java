package com.uc.libtest.adapter;

import android.content.Context;

import com.uc.model.Selectable;
import com.uc.widget.adapter.RecyclerViewAdapterBase;

import java.util.List;

public class ItemAdapter extends RecyclerViewAdapterBase<DataItemHolder> {
    public ItemAdapter(Context context, List<Selectable> items) {
        super(context, items, new DataItemHolderFactory(context));
    }

    @Override
    protected void onAssigned(DataItemHolder holder, Selectable item) {

    }
}
