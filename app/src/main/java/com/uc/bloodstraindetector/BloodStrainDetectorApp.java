package com.uc.bloodstraindetector;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.uc.android.camera.app.CameraApp;
import com.uc.bloodstraindetector.model.CaseItemDao;
import com.uc.bloodstraindetector.model.DaoMaster;
import com.uc.bloodstraindetector.model.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.util.List;

public class BloodStrainDetectorApp extends CameraApp {
    private static final String TAG="BloodStrainDetectorApp";
    public static final String APP_DB = "tixing.db";

    private static BloodStrainDetectorApp instance;
    private LocationManager locationManager;
    private DaoSession daoSession;

    private void setupDao(){
        DaoMaster.OpenHelper helper=new DaoMaster.DevOpenHelper(this, APP_DB);
        Database database = helper.getWritableDb();
        daoSession=new DaoMaster(database).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static BloodStrainDetectorApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance=this;
        setupDao();
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        if (locationManager != null) {
            List<String> providers = locationManager.getAllProviders();
            String locationProvider = null;
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                //TODO: 启动GPS设置
            }
            if (locationProvider != null) {
                return locationManager.getLastKnownLocation(locationProvider);
            }
        }
        return null;
    }

}
