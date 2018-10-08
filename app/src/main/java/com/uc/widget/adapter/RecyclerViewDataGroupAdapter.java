package com.uc.widget.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.uc.bloodstraindetector.R;
import com.uc.model.DataGroup;
import com.uc.model.ItemFinder;
import com.uc.model.Selectable;

import java.util.List;

public abstract class RecyclerViewDataGroupAdapter<HolderType extends RecyclerViewDataGroupHolder, ItemHolderType extends RecyclerViewHolderBase> extends RecyclerViewAdapterBase<HolderType> {
    protected final ViewHolderFactory<ItemHolderType> itemViewViewHolderFactory;
    private OnRecyclerViewGroupedItemClickListener onRecyclerViewGroupedItemClickListener;
    private OnRecyclerViewGroupedItemLongClickListener onRecyclerViewGroupedItemLongClickListener;

    public RecyclerViewDataGroupAdapter(Context context, List<Selectable> items,
                                        ViewHolderFactory<HolderType> viewViewHolderFactory,
                                        ViewHolderFactory<ItemHolderType> itemViewViewHolderFactory){
        super(context, items, viewViewHolderFactory);
        this.itemViewViewHolderFactory = itemViewViewHolderFactory;
    }
    @Override
    public DataGroup getItem(int position){
        return (DataGroup) super.getItem(position);
    }

    protected abstract RecyclerViewAdapterBase<ItemHolderType> createItemAdapter(Context context, List<Selectable> items);
    public abstract DataGroup findGroupBySubItem(Selectable subItem);

    @Override
    protected void onAssigned(HolderType holder, Selectable item) {
        Log.w(TAG, "assigning data to view....");
        final DataGroup group =(DataGroup)item;
        holder.groupNameTextView.setText(group.getGroupName());
    }

    @Override
    public void onBindViewHolder(final HolderType holder, final int groupPosition) {
        Log.d(TAG, "onBindViewHolder: bind group at "+ groupPosition);
        super.onBindViewHolder(holder, groupPosition);
        final DataGroup group=getItem(groupPosition);
        RecyclerViewAdapterBase<ItemHolderType> itemAdapter=createItemAdapter(context, group.getItems());
        holder.itemsRecyclerView.setAdapter(itemAdapter);
        if(onRecyclerViewGroupedItemClickListener !=null) {
            itemAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onClicked(View view, Object tag, int position) {
                    Log.v(TAG, "item clicked at group["+ groupPosition +"] item[" + position +"]");
                    onRecyclerViewGroupedItemClickListener.onClicked(holder.itemView, view, group, groupPosition, tag, position);
                }
            });
        }
        if(onRecyclerViewGroupedItemLongClickListener !=null) {
            itemAdapter.setOnRecyclerViewItemLongClickListener(new OnRecyclerViewItemLongClickListener() {
                @Override
                public boolean onLongClicked(View view, Object tag, int position) {
                    Log.v(TAG, "item long clicked at group["+ groupPosition + "] item[" + position +"]");
                    return onRecyclerViewGroupedItemLongClickListener
                            .onLongClicked(holder.itemView, view, group, groupPosition, tag, position);
                }
            });
        }
    }

    public DataGroup findGroupByName(@NonNull String name){
        for(Selectable item : getItems()){
            if(name.equals(((DataGroup)item).getGroupName())){
                return (DataGroup) item;
            }
        }
        return null;
    }

    public int findGroupIndexByName(@NonNull String name){
        for (Selectable item : getItems()){
            if(name.equals(((DataGroup)item).getGroupName())){
                return getItems().indexOf(item);
            }
        }
        return -1;
    }

    public boolean insertSubItem(Selectable subItem, int position){
        try {
            DataGroup group = findGroupBySubItem(subItem);
            if (group == null) return false;
            HolderType holder = group.getTag();
            RecyclerViewAdapterBase<ItemHolderType> subAdapter = holder.getAdapter();
            subAdapter.insertItem(subItem, position);
            onInsertSubItem(group, subItem);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    protected void onInsertSubItem(DataGroup group, Selectable item){}

    public boolean deleteSubItem(Selectable subItem){
        try {
            DataGroup group=findGroupBySubItem(subItem);
            if(group==null) return false;
            HolderType holder=group.getTag();
            RecyclerViewAdapterBase<ItemHolderType> subAdapter=holder.getAdapter();
            int position=subAdapter.findPositionById(subItem.getId());
            if(position<0) return false;
            subAdapter.deleteItem(position);
            group.getItems().remove(subItem);
            onSubItemDeleted(group, subItem);
            return true;
        } catch (Exception e){
            return false;
        }
    }
    protected void onSubItemDeleted(DataGroup dataGroup, Selectable item){

    }
    public boolean updateSubItem(Selectable subItem){
        try {
            DataGroup group=findGroupBySubItem(subItem);
            if(group==null) return false;
            HolderType holder=group.getTag();
            RecyclerViewAdapterBase<ItemHolderType> subAdapter=holder.getAdapter();
            subAdapter.notifyItemUpdated(subItem);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public void setOnRecyclerViewGroupedItemClickListener(OnRecyclerViewGroupedItemClickListener onRecyclerViewGroupedItemClickListener) {
        this.onRecyclerViewGroupedItemClickListener = onRecyclerViewGroupedItemClickListener;
    }

    public void setOnRecyclerViewGroupedItemLongClickListener(OnRecyclerViewGroupedItemLongClickListener onRecyclerViewGroupedItemLongClickListener) {
        this.onRecyclerViewGroupedItemLongClickListener = onRecyclerViewGroupedItemLongClickListener;
    }
}
