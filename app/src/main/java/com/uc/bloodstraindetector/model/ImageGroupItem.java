package com.uc.bloodstraindetector.model;

import com.uc.bloodstraindetector.view.adapter.ImageListAdapter;
import com.uc.bloodstraindetector.view.adapter.holder.ImageGroupItemHolder;
import com.uc.model.AbstractSelectable;
import com.uc.model.DataGroup;
import com.uc.model.Selectable;

import java.util.ArrayList;
import java.util.List;

public class ImageGroupItem extends AbstractSelectable implements DataGroup {
    private CaseItem caseItem;
    private String name;
    private List<Selectable> items;
    public ImageGroupItem(String date, List<ImageItem> imageItems, CaseItem caseItem){
        this.name=date;
        this.items=new ArrayList<>();
        this.items.addAll(imageItems);
        this.caseItem=caseItem;
    }

    @Override
    public String getGroupName() {
        return name;
    }

    @Override
    public List<Selectable> getItems() {
        return items;
    }

    @Override
    public void selectedAllItems(boolean selected) {
        for(Selectable selectable : items){
            selectable.setSelected(selected);
        }
    }

    @Override
    public int getSelectedCount() {
        int count=0;
        for(Selectable item:items){
            if(item.isSelected()) count ++;
        }
        return  count;
    }

    @Override
    public Long getId() {
        return -1L;
    }

    @Override
    public void setSelectable(boolean selectable) {
        if(isSelectable()!= selectable){
            super.setSelectable(selectable);
            for(Selectable item : items){
                item.setSelectable(selectable);
            }
        }
    }

    public CaseItem getCaseItem() {
        return caseItem;
    }

    public void add(Selectable item){
        items.add(item);
    }

    public ImageListAdapter getSubAdapter(){
        ImageGroupItemHolder holder=getTag();
        if(holder!=null){
            return (ImageListAdapter) holder.getAdapter();
        }
        return null;
    }

    @Override
    public String toString() {
        return "group id=[" + getId() + "]" +
                "name=["+ getGroupName() +"]" +
                "item count=["+ getItems().size() +"]";
    }
}
