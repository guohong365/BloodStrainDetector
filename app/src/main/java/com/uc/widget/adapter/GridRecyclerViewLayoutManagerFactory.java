package com.uc.widget.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by guoho on 2017/9/25.
 */

public class GridRecyclerViewLayoutManagerFactory implements RecyclerViewLayoutManagerFactory {
    Context context;
    int columns;
    int orientation;
    public GridRecyclerViewLayoutManagerFactory(Context context, int columns, int orientation){
        this.context=context;
        this.columns=columns;
        this.orientation=orientation;
    }
    @Override
    public RecyclerView.LayoutManager create() {
        return new GridLayoutManager(context, columns, orientation, false);
    }
}
