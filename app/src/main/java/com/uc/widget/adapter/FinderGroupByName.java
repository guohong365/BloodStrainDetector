package com.uc.widget.adapter;

import com.uc.model.DataGroup;
import com.uc.model.ItemFinder;

import java.util.List;

public class FinderGroupByName implements ItemFinder<DataGroup> {
    public DataGroup find(List<? extends DataGroup> collection, String name) {
        for (DataGroup group : collection){
            if(group.getGroupName().equals(name)) return group;
        }
        return null;
    }

    @Override
    public DataGroup find(List<? extends DataGroup> collection, Object... args) {
        if(args.length==0) return null;
        if(args[0]==null) return null;
        if(!(args[0] instanceof String)) return null;
        return find(collection, (String)args[0]);
    }
}
