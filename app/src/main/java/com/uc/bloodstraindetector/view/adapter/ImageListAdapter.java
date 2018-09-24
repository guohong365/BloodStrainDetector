package com.uc.bloodstraindetector.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.uc.bloodstraindetector.R;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageItem;
import com.uc.bloodstraindetector.view.adapter.holder.ImageItemHolder;
import com.uc.model.Selectable;
import com.uc.utils.FileHelper;
import com.uc.widget.adapter.RecyclerViewAdapterBase;
import com.uc.widget.adapter.RecyclerViewHolderBase;
import com.uc.widget.adapter.ViewHolderFactory;

import java.io.File;
import java.util.List;

public class ImageListAdapter extends RecyclerViewAdapterBase<ImageItemHolder> {

    private int current;

    public ImageListAdapter(Context context, List<Selectable> items, ViewHolderFactory<ImageItemHolder> factory) {
        super(context, items, factory);
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getCurrent() {
        return current;
    }

    @Override
    protected void onAssigned(ImageItemHolder holder, Selectable item) {
        ImageItem imageItem=(ImageItem)item;
        Glide.with(context)
                .load(imageItem.getUri())
                .override(256, 256)
                .into(holder.image);
    }

    @Override
    protected void onDeleteItem(Selectable item) {
        Log.d(TAG, "onDeleteItem: item=" + item.toString());
        File file= FileHelper.getUriFile(context, ((ImageItem)item).getUri());
        file.delete();
        DataManager.getInstance().deleteImageItem((ImageItem) item);
    }

    @Override
    protected void onInsertItem(Selectable item) {
        Log.d(TAG, "onInsertItem: item=" + item.toString());
        DataManager.getInstance().insertImageItem((ImageItem) item);

    }

    @Override
    protected void onReplaceItem(Selectable oldItem, Selectable newItem) {
        Log.d(TAG, "onReplaceItem: oldItem =" + oldItem.toString());
        Log.d(TAG, "onReplaceItem: newItem =" + newItem.toString());
        ImageItem item=DataManager.getInstance().findImageById(oldItem.getId());
        if(item==null){
            throw new IllegalArgumentException();
        }
        ImageItem newImage= (ImageItem) newItem;
        item.setSize(newImage.getSize());
        item.setTakenTime(newImage.getTakenTime());
        item.setLongitude(newImage.getLongitude());
        item.setLatitude(newImage.getLatitude());
        item.setUri(newImage.getUri());
        item.setHeight(newImage.getHeight());
        item.setWidth(newImage.getWidth());
        DataManager.getInstance().updateImageItem(item);
    }
}
