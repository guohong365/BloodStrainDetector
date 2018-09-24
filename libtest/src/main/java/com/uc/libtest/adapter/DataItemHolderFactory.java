package com.uc.libtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.uc.libtest.R;
import com.uc.widget.adapter.ViewHolderFactoryBase;


public class DataItemHolderFactory extends ViewHolderFactoryBase<DataItemHolder> {

    public DataItemHolderFactory(Context context) {
        super(context);
    }

    @Override
    public DataItemHolder create(ViewGroup parent, int viewType) {
        return new DataItemHolder(LayoutInflater.from(context).inflate(R.layout.view_item, parent, false));
    }
}
