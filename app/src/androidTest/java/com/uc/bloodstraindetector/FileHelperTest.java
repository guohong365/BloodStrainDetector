package com.uc.bloodstraindetector;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.uc.bloodstraindetector.utils.FileHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class FileHelperTest {
    static final String TAG="FileHelperTest";
    @Test
    public void test(){
        Context appContext= InstrumentationRegistry.getContext();
        File file= FileHelper.getDir(appContext, null);
        Log.d(TAG, "test: AppDataRoot=" + file.getAbsolutePath());
        file=FileHelper.getDir(appContext, Environment.DIRECTORY_DOCUMENTS);
        Log.d(TAG, "test: App Doc=" + file.getAbsolutePath());
        file=FileHelper.getCacheDir(appContext);
        Log.d(TAG, "test: cache dir=" + file.getAbsolutePath());
        file=FileHelper.getTempFile(appContext, UUID.randomUUID().toString());
        Log.d(TAG, "test: temp file=" + file.getAbsolutePath());
        file=FileHelper.getImageDir(appContext);
        Log.d(TAG, "test: image dir=" + file.getAbsolutePath());
        file=FileHelper.getImageFile(appContext, UUID.randomUUID().toString() + ".jpeg");
        Log.d(TAG, "test: image file=" + file.getAbsolutePath());

        file= appContext.getExternalFilesDir(null);
        Log.d(TAG, "test: external dir=" + file.getAbsolutePath());
        file= appContext.getFilesDir();
        Log.d(TAG, "test: internal dir=" + file.getAbsolutePath());

        Uri uri = FileHelper.getImageUri(appContext, "test.jpeg");
        Log.d(TAG, "test: path=" + uri.getPath() + ", toString=" + uri.toString());

        uri= Uri.parse(uri.toString());
        Log.d(TAG, "test: " + uri.toString());

        file = FileHelper.getUriFile(appContext, uri);
        Log.d(TAG, "test: file path=" + file.getAbsolutePath());
    }
}
