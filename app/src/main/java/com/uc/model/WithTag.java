package com.uc.model;

public interface WithTag {
    <T> T getTag();
    void setTag(Object tag);
}
