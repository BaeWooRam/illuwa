package com.geek.soft.illuwa.ui;

import android.app.Application;
import android.os.SystemClock;

import com.nhn.android.maps.NMapView;

import java.util.concurrent.TimeUnit;

public class IlluwaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(TimeUnit.SECONDS.toMillis(2));

    }
}
