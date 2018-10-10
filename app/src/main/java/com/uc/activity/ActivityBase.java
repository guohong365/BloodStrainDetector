package com.uc.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.uc.bloodstraindetector.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

public abstract class ActivityBase extends QuickActivity {
    enum FULL_SCREEN_MODE{
        NORMAL,
        NO_ACTION_BAR,
        FULL_SCREEN_WITH_NAVIGATION,
        FULL_SCREEN_HIDE_ALL
    }
    class ProcessorPair{
        public ResultProcessor resultProcessor;
        public FailureProcessor failureProcessor;
        public ProcessorPair(ResultProcessor resultProcessor, FailureProcessor failureProcessor){
            this.resultProcessor=resultProcessor;
            this.failureProcessor=failureProcessor;
        }
    }
    private static final int REQUEST_PERMISSION=0;
    private Map<Integer, ProcessorPair> processors;
    protected FULL_SCREEN_MODE fullScreenMode;
    private Toast mToast;
    public ActivityBase(){
        processors=new HashMap<>();
        fullScreenMode=FULL_SCREEN_MODE.NORMAL;
    }
    public void setFullScreenMode(FULL_SCREEN_MODE fullScreenMode) {
        this.fullScreenMode = fullScreenMode;
    }

    public void addProcessor(int requestCode,@NonNull ResultProcessor resultProcessor, FailureProcessor failureProcessor){
        processors.put(requestCode,new ProcessorPair(resultProcessor, failureProcessor));
    }

    public void addProcessor(int requestCode,@NonNull ResultProcessor resultProcessor){
        addProcessor(requestCode, resultProcessor, null);
    }

    protected void startActivityForResult(RequestParams params, Class<?> activityClass){
        Intent intent=new Intent();
        intent.setClass(this, activityClass);
        intent.putExtra(RequestParams.KEY_REQUEST, params);
        startActivityForResult(intent, params.getAction());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ProcessorPair processorPair=processors.getOrDefault(requestCode, null);
        if(processorPair!=null) {
            if (resultCode != RESULT_OK && processorPair.failureProcessor!=null) {
                processorPair.failureProcessor.process();
            } else {
                RequestParams params=data.getExtras().getParcelable(RequestParams.KEY_REQUEST);
                processorPair.resultProcessor.process(data, params);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void requestPermissions(List<String> unAuthorized){
        String[] array=new String[unAuthorized.size()];
        unAuthorized.toArray(array);
        ActivityCompat.requestPermissions(this, array, REQUEST_PERMISSION);
    }

    protected void onPermissionSatisfied(){

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            View decorView = getWindow().getDecorView();
            ActionBar bar=getSupportActionBar();
            int options;
            switch (fullScreenMode) {
                case NO_ACTION_BAR:
                    if (bar != null) bar.hide();
                    break;
                case FULL_SCREEN_WITH_NAVIGATION:
                    options = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(options);
                    if (bar != null) bar.hide();
                    break;
                case FULL_SCREEN_HIDE_ALL:
                    options = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    decorView.setSystemUiVisibility(options);
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                    getWindow().setNavigationBarColor(Color.TRANSPARENT);
                    if(bar!=null)bar.hide();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean error=false;
        switch (requestCode){
            case REQUEST_PERMISSION:
                if(grantResults.length >0){
                    for(int i=0; i<grantResults.length; i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            Log.d(TAG, "onRequestPermissionsResult: item[" + i + "] failed.");
                            Toast.makeText(this, R.string.permission_not_satisfied, Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.permission_requirement_cancelled, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                break;
        }
        onPermissionSatisfied();
    }

    protected void checkAndRequestPermission(String[] neededPermissions){
        List<String> unAuthorized=new ArrayList<>();
        for(String item : neededPermissions){
            if(ContextCompat.checkSelfPermission(this, item)!= PackageManager.PERMISSION_GRANTED){
                unAuthorized.add(item);
            }
        }
        if(unAuthorized.size()>0){
            requestPermissions(unAuthorized);
        }
    }

    public void showToast(String text){
        if(mToast!=null){
            mToast.cancel();
        }
        mToast=Toast.makeText(this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }
    public void showToast(int resId){
        if(mToast!=null){
            mToast.cancel();
        }
        mToast=Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void hideStatusBarAndActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        int option=View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(option);
    }
    public void setStatusBaseTransparent(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public void hideNavigationBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        int option=View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(option);
    }

    public void hideNavigationBarAndStatusBar(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    public void setNavigationBarStatusBarTranslucent(){
        int option= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        getWindow().getDecorView().setSystemUiVisibility(option);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }
}
