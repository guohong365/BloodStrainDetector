package com.uc.bloodstraindetector.view.adapter.holder;

import android.view.View;

public class ImageChooseHolder extends ImageItemHolder{
    public ImageChooseHolder(View itemView) {
        super(itemView, -1);
        itemView.setClickable(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
