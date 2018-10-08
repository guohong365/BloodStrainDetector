package com.uc.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

public class ActivityUtils {
    public static void SetFullScreen(Activity activity, FullScreenMode mode){
        if(activity.hasWindowFocus()) {
            View decorView=activity.getWindow().getDecorView();
            ActionBar bar=activity.getActionBar();
            int options;
            switch (mode) {
                case NORMAL:
                    break;
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
                    activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
                    activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
                    if(bar!=null) bar.hide();
                    break;
            }
        }
    }

    public static void HideStatusBarAndActionBar(Activity activity){
        int options=View.SYSTEM_UI_FLAG_FULLSCREEN;
        View decorView=activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(options);
        ActionBar actionBar=activity.getActionBar();
        if(actionBar!=null) actionBar.hide();
    }

    public static void SetStatusBarTransparent(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public static void HideNavigationBar(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        int option=SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(option);
    }

    public void hideNavigationBarAndStatusBar(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    public void setNavigationBarStatusBarTranslucent(Activity activity){
        View decorView=activity.getWindow().getDecorView();
        int option= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        ActionBar actionBar=activity.getActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

}
