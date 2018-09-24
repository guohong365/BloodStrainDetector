package com.uc.libtest.adapter;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;

import com.uc.libtest.R;
import com.uc.widget.adapter.RecyclerViewDataGroupHolder;


public class GroupItemHolder extends RecyclerViewDataGroupHolder {

    public GroupItemHolder(View itemView) {
        super(itemView, R.id.t_group_name, R.id.ctrl_group);
        itemView.setLongClickable(true);
        itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater=((Activity)v.getContext()).getMenuInflater();
                inflater.inflate(R.menu.context_menu_group, menu);
            }
        });
    }
}
