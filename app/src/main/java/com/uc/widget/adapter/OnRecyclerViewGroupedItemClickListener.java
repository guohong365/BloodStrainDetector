package com.uc.widget.adapter;

import android.view.View;

import com.uc.model.DataGroup;

public interface OnRecyclerViewGroupedItemClickListener {
    void onClicked(View groupView, View view, DataGroup group, int groupPosition, Object tag, int position);
}
