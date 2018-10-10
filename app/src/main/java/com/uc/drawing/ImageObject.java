package com.uc.drawing;

import android.graphics.Bitmap;
import android.graphics.Path;

public interface ImageObject extends Rectangle {
    Bitmap getImage();
    void setImage(Bitmap image);
    void setClip(Path region);
}
