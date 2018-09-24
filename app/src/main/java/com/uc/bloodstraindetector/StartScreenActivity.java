package com.uc.bloodstraindetector;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.uc.activity.ActivityBase;

public class StartScreenActivity extends ActivityBase implements Runnable{
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private static final int MESSAGE_INIT_ERROR=1000;
    private static final String TAG="StartScreenActivity";

    private final Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==MESSAGE_INIT_ERROR){
                showError();
                finish();
                return;
            }
            super.handleMessage(msg);
        }
    };

    private void showError(){
        Toast.makeText(this, R.string.err_init_app, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreateTasks(Bundle savedInstanceState) {
        hideNavigationBarAndStatusBar();
        setContentView(R.layout.activity_start_screen);
        handler.postDelayed(this, AUTO_HIDE_DELAY_MILLIS);
    }

    private void startMainActivity(){
        Intent intent=new Intent();
        intent.setClass(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public void run() {
        try {
            Application application=getApplication();
            Log.d(TAG, application.getClass().getName());
        }catch (Exception e){
            e.printStackTrace();
            handler.sendEmptyMessage(MESSAGE_INIT_ERROR);
            return;
        }
        startMainActivity();
    }
}
