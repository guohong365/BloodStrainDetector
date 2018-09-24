package com.uc.activity;

import android.os.Parcelable;

public interface RequestParams extends Parcelable{
    String KEY_REQUEST="KEY_REQUEST";
    int getAction();
    void setAction(int action);
}
