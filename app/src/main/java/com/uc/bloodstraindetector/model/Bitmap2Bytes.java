package com.uc.bloodstraindetector.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.io.ByteArrayOutputStream;

public class Bitmap2Bytes implements PropertyConverter<Bitmap, byte[]> {
    @Override
    public Bitmap convertToEntityProperty(byte[] databaseValue) {
        return BitmapFactory.decodeByteArray(databaseValue, 0, databaseValue.length);
    }

    @Override
    public byte[] convertToDatabaseValue(Bitmap entityProperty) {
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        entityProperty.compress(Bitmap.CompressFormat.JPEG, 100, os);
        return os.toByteArray();
    }
}
