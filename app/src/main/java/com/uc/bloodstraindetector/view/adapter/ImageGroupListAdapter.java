package com.uc.bloodstraindetector.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.uc.bloodstraindetector.model.CaseItem;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageGroupItem;
import com.uc.bloodstraindetector.model.ImageItem;
import com.uc.bloodstraindetector.view.adapter.holder.ImageGroupItemHolder;
import com.uc.bloodstraindetector.view.adapter.holder.ImageItemHolder;
import com.uc.model.DataGroup;
import com.uc.model.Selectable;
import com.uc.widget.adapter.RecyclerViewAdapterBase;
import com.uc.widget.adapter.RecyclerViewDataGroupAdapter;
import com.uc.widget.adapter.ViewHolderFactory;

import java.util.ArrayList;
import java.util.List;

public class ImageGroupListAdapter extends RecyclerViewDataGroupAdapter<ImageGroupItemHolder, ImageItemHolder> {
    private static final String TAG="ImageGroupListAdapter";

    public ImageGroupListAdapter(Context context, List<ImageGroupItem> items, ViewHolderFactory<ImageGroupItemHolder> groupItemHolderFactory, ViewHolderFactory<ImageItemHolder> itemHolderFactory) {
        super(context, new ArrayList<>(items), groupItemHolderFactory, itemHolderFactory);
    }

    @Override
    public ImageGroupItem getItem(int position) {
        return (ImageGroupItem) super.getItem(position);
    }

    @Override
    protected RecyclerViewAdapterBase<ImageItemHolder> createItemAdapter(Context context, List<Selectable> items) {
        ImageListAdapter adapter=new ImageListAdapter(context, items, itemViewViewHolderFactory);
        return adapter;
    }

    @Override
    public DataGroup findGroupBySubItem(Selectable subItem) {
        ImageItem imageItem= (ImageItem) subItem;
        return findGroupByName(((ImageItem) subItem).getTakenDate());
    }

    @Override
    public ImageGroupItem findGroupByName(@NonNull String name) {
        return (ImageGroupItem) super.findGroupByName(name);
    }

    @Override
    protected void onInsertItem(Selectable item) {
        Log.d(TAG, "onInsertItem: group was inserted. " + item.toString());
    }

    @Override
    protected void onSubItemDeleted(DataGroup dataGroup, Selectable item){
        if(dataGroup.getItems().size()==0) {
            int position = findGroupIndexByName(dataGroup.getGroupName());
            deleteItem(position);
        }
    }

    @Override
    public boolean insertSubItem(Selectable subItem, int position) {
        Log.d(TAG, "insertSubItem:.......");
        ImageItem imageItem=(ImageItem)subItem;
        ImageGroupItem groupItem=findGroupByName(imageItem.getTakenDate());
        if(groupItem==null){
            Log.d(TAG, "insertSubItem: group["+imageItem.getTakenDate()+"] not found.");
            CaseItem caseItem=DataManager.getInstance().findCaseById(imageItem.getCaseId());
            DataManager.getInstance().insertImageItem(imageItem);
            List<ImageItem> list=new ArrayList<>();
            list.add(imageItem);
            groupItem=new ImageGroupItem(imageItem.getTakenDate(), list, caseItem);
            Log.d(TAG, "insertSubItem: insert new group." + groupItem.toString());
            insertItem(groupItem, 0);
            return true;
        } else {
            Log.d(TAG, "insertSubItem:group found. just call supper....");
            return super.insertSubItem(subItem, position);
        }
    }
/*
    public boolean insertImageItem(ImageItem item){
        Log.d(TAG, "insertImageItem: ..........");
        ImageGroupItem groupItem=null;
        try{
            DataManager.getInstance().insertImageItem(item);
            if(!insertSubItem(item, 0)){
                Log.d(TAG, "insertImageItem: 未找到对应组，新件组.....");
                List<ImageItem> list =new ArrayList<>();
                list.add(item);
                CaseItem caseItem=DataManager.getInstance().findCaseById(item.getCaseId());
                groupItem=new ImageGroupItem(item.getTakenDate(), list, caseItem);
                insertItem(groupItem, 0);
            }
            return  true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
*/
}
