package com.uc.libtest.model;

import com.uc.model.AbstractSelectable;

public class DataItem extends AbstractSelectable {
    static long sequence=0;
    private final long id;
    private final long groupId;
    private final String text;

    public DataItem(long groupId, String text) {
        this.id = sequence ++;
        this.groupId = groupId;
        this.text = text;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public long getGroupId() {
        return groupId;
    }
}
