package com.uc.bloodstraindetector.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uc.android.camera.util.FileUtil;
import com.uc.bloodstraindetector.R;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageItem;
import com.uc.bloodstraindetector.utils.FileHelper;
import com.yalantis.ucrop.view.GestureCropImageView;

import java.io.File;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {
    private static final String TAG="ImagePagerAdapter";
    private final List<ImageItem> imageItems;
    private final Context context;

    public ImagePagerAdapter(Context context, List<ImageItem> imageItems){
        this.context=context;
        this.imageItems=imageItems;
    }

    @Override
    public int getCount() {
        return imageItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        ImageItem imageItem=imageItems.get(position);
        View view= LayoutInflater.from(context).inflate(R.layout.item_image_pager_content, null);
        GestureCropImageView photoVIew=view.findViewById(R.id.i_image);
        TextView textView=view.findViewById(R.id.t_image_info);
        photoVIew.setEnabled(true);
        photoVIew.setScaleEnabled(true);
        photoVIew.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        photoVIew.setImageURI(imageItem.getUri());
        File file= FileHelper.getUriFile(context,imageItem.getUri());
        textView.setText(file.getName());
        view.setTag(imageItem);
        container.addView(view);
        Log.d(TAG, "instantiateItem: " + imageItem.getUri().toString());
        Log.d(TAG, "instantiateItem: file[" + file.getName() +  "] exists=" + file.exists());
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view=(View)object;
        container.removeView(view);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        View view=(View)object;
        Log.d(TAG, "getItemPosition: " + view.getTag());
        int index=imageItems.indexOf(view.getTag());
        return index >=0 ? index : POSITION_NONE;
    }

    public void deleteItem(int position){
        imageItems.remove(position);
    }
}
