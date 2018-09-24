package com.uc.bloodstraindetector.view.adapter.holder;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import com.uc.bloodstraindetector.R;
import com.uc.widget.adapter.RecyclerViewHolderBase;

public class ImageItemHolder extends RecyclerViewHolderBase {
    public final ImageView image;
    public final ImageView imageOverlay;
    public final ImageView checkbox;

    public ImageItemHolder(View itemView, final int context_menu) {
        super(itemView);
        image=itemView.findViewById(R.id.i_image);
        imageOverlay=itemView.findViewById(R.id.i_image_overlay);
        checkbox=itemView.findViewById(R.id.ctrl_selected_check);
        if(context_menu>0) {
            itemView.setLongClickable(true);
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    MenuInflater menuInflater = ((Activity) itemView.getContext()).getMenuInflater();
                    menuInflater.inflate(context_menu, menu);
                }
            });
        }
    }

    @Override
    protected void onSelectedChanged() {
        //imageOverlay.setSelected(isSelected());
        //checkbox.setSelected(isSelected());
        itemView.setSelected(isSelected());
    }

    @Override
    protected void onSelectableChanged() {
        imageOverlay.setVisibility(isSelectable() ? View.VISIBLE :View.INVISIBLE);
        checkbox.setVisibility(isSelectable() ? View.VISIBLE : View.INVISIBLE);
    }
}
