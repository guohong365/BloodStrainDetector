package com.uc.bloodstraindetector;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.uc.activity.ActivityBase;
import com.uc.android.camera.settings.CameraSettingsActivity;

public class MainActivity extends ActivityBase implements View.OnClickListener {

    private static String[] neededPermissions=new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreateTasks(Bundle savedInstanceState) {
        super.onCreateTasks(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermission(neededPermissions);
        View view=findViewById(R.id.action_case_view);
        view.setOnClickListener(this);
        view=findViewById(R.id.action_settings);
        view.setOnClickListener(this);
        view=findViewById(R.id.action_gallery);
        view.setOnClickListener(this);
    }

    @Override
    protected void onPermissionSatisfied() {
        LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager!=null){
            ((BloodStrainDetectorApp)getApplication()).setLocationManager(locationManager);
        }
    }

    @Override
    public void onClick(View v) {
        Class<?> clazz=null;
        switch (v.getId()){
            case R.id.action_case_view:
                clazz=CaseListActivity.class;
                break;
            case R.id.action_settings:
                clazz=CameraSettingsActivity.class;
                break;
            case R.id.action_gallery:
                clazz=GalleryActivity.class;
                break;
        }
        if(clazz!=null){
            Intent intent=new Intent();
            intent.setClass(this, clazz);
            startActivity(intent);
        }
    }
}
