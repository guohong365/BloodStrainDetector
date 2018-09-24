package com.uc.libtest.adapter;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.uc.libtest.R;
import com.uc.widget.adapter.RecyclerViewHolderBase;



public class DataItemHolder extends RecyclerViewHolderBase {
    public final TextView t_id;
    public final TextView t_text;
    public DataItemHolder(View view){
        super(view);
        t_id=view.findViewById(R.id.t_data_id);
        t_text=view.findViewById(R.id.t_data_text);
        view.setLongClickable(true);
        view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater=((Activity)v.getContext()).getMenuInflater();
                inflater.inflate(R.menu.context_menu_item, menu);
            }
        });
    }

}
