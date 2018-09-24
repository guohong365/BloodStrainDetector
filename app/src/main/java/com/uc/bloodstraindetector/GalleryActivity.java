package com.uc.bloodstraindetector;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.uc.activity.ActivityBase;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageItem;
import com.uc.bloodstraindetector.view.adapter.ImageListAdapter;
import com.uc.bloodstraindetector.view.adapter.holder.ImageItemHolderFactory;
import com.uc.widget.adapter.GridDecoration;
import com.uc.widget.adapter.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends ActivityBase {
    private ImageListAdapter imageAdapter;
    private List<ImageItem> imageItems;
    @Override
    protected void onCreateTasks(Bundle savedInstanceState) {
        setContentView(R.layout.activity_gallery);
        imageItems= DataManager.getInstance().getImages();
        RecyclerView view=findViewById(R.id.ctrl_image_list);
        int column=getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE ?
                getResources().getInteger(R.integer.image_grid_horizontal_span) :
                getResources().getInteger(R.integer.image_grid_vertival_span);
        Log.d(TAG, "onCreateTasks: column=" + column);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, column, LinearLayout.VERTICAL, false);
        view.setLayoutManager(gridLayoutManager);
        view.addItemDecoration(new GridDecoration(getResources().getDimensionPixelSize(R.dimen.thick_image_item_grid_decoration),getColor(R.color.black_overlay)));
        //view.addItemDecoration(new GridDecoration(getResources().getDimensionPixelSize(R.dimen.grid_divider), getColor(R.color.red)));
        imageAdapter=new ImageListAdapter(this, new ArrayList<>(imageItems), new ImageItemHolderFactory(this,-1, column));
        imageAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClicked(View view, Object tag, int position) {
                startImagePager(position);
            }
        });
        view.setAdapter(imageAdapter);
    }

    private void startImagePager(int position){
        Intent intent=new Intent();
        intent.setClass(this,ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.KEY_CASE_ID, -1);
        intent.putExtra(ImagePagerActivity.KEY_POSITION, position);
        intent.putExtra(ImagePagerActivity.KEY_VIEW_ONLY, true);
        startActivity(intent);
    }
}
