package com.uc.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;

public class FileHelper {
    private static final String TAG="FileHelper";
    public static final String IMAGE_EXT=".jpeg";
    public static File getUriFile(Context context, Uri uri){
        try{
            Log.d(TAG, "getUriFile: uri authority=[" + uri.getAuthority() +"]");
            if(uri.getAuthority().isEmpty()){
                Log.d(TAG, "getUriFile: uri path=[" + uri.getPath() + "]");
                return new File(uri.getPath());
            }
            List<PackageInfo> packages=context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            String fileProviderClassName=FileProvider.class.getName();
            for (PackageInfo packageInfo : packages){
                ProviderInfo [] providers = packageInfo.providers;
                if(providers==null) {
                    continue;
                }
                Log.d(TAG, "getUriFile: [" + providers.length + "] providers in package[" + packageInfo.packageName + "]");
                for(ProviderInfo provider : providers){
                    if(!uri.getAuthority().equals(provider.authority)) {
                        continue;
                    }
                    Log.d(TAG, "getUriFile: provider authority matched to [" + provider.authority +  "]");
                    if(!provider.name.equalsIgnoreCase(fileProviderClassName)) {
                        continue;
                    }
                    Log.d(TAG, "getUriFile: provider class matched to [" + provider.name + "].");
                    Class<FileProvider> fileProviderClass=FileProvider.class;
                    Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                    Log.d(TAG, "getUriFile: method [getPathStrategy] found.");
                    getPathStrategy.setAccessible(true);
                    Object pathStrategyClassInstance=getPathStrategy.invoke(null, context, uri.getAuthority());
                    Log.d(TAG, "getUriFile: [PathStrategy] instance got.");
                    String pathStrategyClassName=fileProviderClassName + "$PathStrategy";
                    Class<?> pathStrategyClass=Class.forName(pathStrategyClassName);
                    Log.d(TAG, "getUriFile: class [" + pathStrategyClassName + "] got." );
                    Method getFileFromUri = pathStrategyClass.getDeclaredMethod("getFileForUri", Uri.class);
                    Log.d(TAG, "getUriFile: method [getFileForUri] found.");
                    getFileFromUri.setAccessible(true);
                    Object fileInstance = getFileFromUri.invoke(pathStrategyClassInstance, uri);
                    Log.d(TAG, "getUriFile: [File] instance got.[" +(fileInstance==null ? "null": ((File)fileInstance).getAbsolutePath()) + "]");
                    return (File)fileInstance;
                }
            }
        }catch (Exception e){
            Log.d(TAG, "getUriFile: EXCEPTION " + e.toString());
        }
        return null;
    }

    public static File getDir(Context context, String subDir){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            return context.getExternalFilesDir(subDir);
        }
        File file=new File(context.getFilesDir(), subDir);
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }

    public static File getCacheDir(Context context){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            return context.getExternalCacheDir();
        }
        return context.getCacheDir();
    }

    public static File getFile(Context context, String dir, String fileName){
        File file= new File(getDir(context, dir), fileName);
        return file;
    }

    public static File getTempFile(Context context, String fileName){
        return new File(getCacheDir(context), fileName);
    }

    public static Uri getFileUri(Context context, String authority, String dir, String fileName){
        return FileProvider.getUriForFile(context, authority, getFile(context,dir, fileName));
    }
    public static Uri getTempFileUri(Context context, String authority, String fileName){
        return FileProvider.getUriForFile(context, authority, getTempFile(context, fileName));
    }
}
