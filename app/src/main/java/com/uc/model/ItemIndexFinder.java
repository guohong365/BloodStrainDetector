package com.uc.model;

import java.util.List;

public interface ItemIndexFinder<T> {
    int find(List<? extends T> collection, Object... args);
}
