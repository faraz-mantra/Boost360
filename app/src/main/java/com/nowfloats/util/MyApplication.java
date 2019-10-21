package com.nowfloats.util;

import android.app.Application;

import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks;
import com.webengage.sdk.android.WebEngageConfig;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        initWebEngage();

    }


    void initWebEngage(){


        WebEngageConfig webEngageConfig = new WebEngageConfig.Builder()
                .setWebEngageKey("UQNe5yhVnTtyVi6RGRFAlvG9WFEVagA6")
                .setDebugMode(true) // only in development mode
                .build();
        registerActivityLifecycleCallbacks(new WebEngageActivityLifeCycleCallbacks(this, webEngageConfig));


    }
}
