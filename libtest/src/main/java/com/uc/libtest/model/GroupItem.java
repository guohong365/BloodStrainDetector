package com.uc.libtest.model;

import com.uc.model.AbstractSelectable;
import com.uc.model.DataGroup;
import com.uc.model.Selectable;

import java.util.ArrayList;
import java.util.List;

public class GroupItem extends AbstractSelectable implements DataGroup {
    private static long sequence=0;
    private final long id;
    private final String name;
    private final List<Selectable> items;
    public GroupItem(){
        this.id=sequence++;
        name="GROUP " + id;
        items=new ArrayList<>();
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
        for(Selectable item : items){
            item.setSelected(true);
        }
    }

    @Override
    public int getSelectedCount() {
        int count=0;
        for(Selectable item : items){
            if(item.isSelected()) count ++;
        }
        return 0;
    }

    @Override
    public Long getId() {
        return id;
    }
}
