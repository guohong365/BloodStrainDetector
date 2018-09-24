package com.uc.model;

import java.util.List;

public interface ItemFinder<T> {
    T find(List<? extends T> collection, Object... args);
}
