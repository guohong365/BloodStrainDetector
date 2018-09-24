package com.uc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.widget.LinearLayout;

public class ActivityHelper {
    //@param orientation must be one of ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE and ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    //@see ActivityInfo
    public static void forceLayoutOrientation(Activity activity, int orientation){
        if(activity.getRequestedOrientation()!=orientation){
            activity.setRequestedOrientation(orientation);
        }
    }
}
