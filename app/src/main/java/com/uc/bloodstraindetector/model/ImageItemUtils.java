package com.uc.bloodstraindetector.model;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import com.uc.bloodstraindetector.utils.FileHelper;

import java.io.File;
import java.util.Date;

public class ImageItemUtils {
    private static final String TAG="ImageItemUtils";
    public static void fillImageExif(Context context,ImageItem imageItem){
        File file= FileHelper.getUriFile(context,imageItem.getUri());
        if(file==null) return;
        try {
            ExifInterface exifInterface=new ExifInterface(file.getAbsolutePath());
            float[] latLong=new float[2];
            exifInterface.getLatLong(latLong);
            imageItem.setLatitude((double)latLong[0]);
            imageItem.setLongitude((double)latLong[1]);
            imageItem.setWidth(exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0));
            imageItem.setHeight(exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0));
            imageItem.setSize(file.length());
        } catch (Exception e){
            Log.d(TAG, "fillImageExif: " + e.toString());
        }
    }
    public static ImageItem New(Long caseId, Uri imageUri){
        ImageItem imageItem=new ImageItem();
        imageItem.setUri(imageUri);
        imageItem.setCaseId(caseId);
        imageItem.setTakenTime(new Date());
        return imageItem;
    }
}
