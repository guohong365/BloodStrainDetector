package com.uc.drawing;

import android.graphics.Canvas;

public interface Render<T extends DrawObject> {
    void render(T object, Canvas canvas);
}
