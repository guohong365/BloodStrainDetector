package com.uc.bloodstraindetector.view.adapter.holder;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uc.bloodstraindetector.R;
import com.uc.widget.adapter.RecyclerViewHolderBase;

public class CaseItemHolder extends RecyclerViewHolderBase {
    public final TextView name;
    public final TextView description;
    public final ImageView level;
    public final ImageView preview;
    public CaseItemHolder(View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.t_name);
        description=itemView.findViewById(R.id.t_description);
        level=itemView.findViewById(R.id.i_level);
        preview=itemView.findViewById(R.id.i_preview);
        itemView.setLongClickable(true);
        itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater menuInflater= ((Activity)itemView.getContext()).getMenuInflater();
                menuInflater.inflate(R.menu.context_menu_case_list_item, menu);
            }
        });
    }
}
