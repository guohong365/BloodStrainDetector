package com.uc.widget.adapter;

import android.view.ViewGroup;

public interface ViewHolderFactory<ViewHolderType> {
    ViewHolderType create(ViewGroup parent, int viewType);
}
