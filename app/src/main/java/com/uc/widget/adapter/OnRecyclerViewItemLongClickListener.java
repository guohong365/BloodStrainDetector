package com.uc.widget.adapter;

import android.view.View;

public interface OnRecyclerViewItemLongClickListener {
    boolean onLongClicked(View view, Object tag, int position);
}
