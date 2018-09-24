package com.uc.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class RecyclerViewDataGroupHolder extends RecyclerViewHolderBase {
    public final TextView groupNameTextView;
    public final RecyclerView itemsRecyclerView;
    public RecyclerViewDataGroupHolder(View itemView, int groupNameTextView, int itemsRecyclerViewId) {
        super(itemView);
        this.groupNameTextView =(TextView) itemView.findViewById(groupNameTextView);
        itemsRecyclerView=(RecyclerView)itemView.findViewById(itemsRecyclerViewId);
    }
    public <T> T getAdapter(){
        return (T)itemsRecyclerView.getAdapter();
    }
}
