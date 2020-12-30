package com.nowfloats.util;

import android.content.Context;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import java.util.Map;

public class AppsFlyerUtils implements AppsFlyerConversionListener {
    private static final String TAG = AppsFlyerUtils.class.getName();

    public static void initAppsFlyer(Context context, String devKey) {
        AppsFlyerLib.getInstance().init(devKey, new AppsFlyerUtils(), context);
        //Start the SDK
        AppsFlyerLib.getInstance().start(context);
        //Enable Debugging
        AppsFlyerLib.getInstance().setDebugLog(true);
    }

    @Override
    public void onConversionDataSuccess(Map<String, Object> conversionData) {
        for (String attrName : conversionData.keySet()) {
            Log.d(TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
        }
    }

    @Override
    public void onConversionDataFail(String errorMessage) {
        Log.d(TAG, "error getting conversion data: " + errorMessage);
    }

    @Override
    public void onAppOpenAttribution(Map<String, String> attributionData) {
        for (String attrName : attributionData.keySet()) {
            Log.d(TAG, "attribute: " + attrName + " = " + attributionData.get(attrName));
        }
    }

    @Override
    public void onAttributionFailure(String errorMessage) {
        Log.d(TAG, "error onAttributionFailure : " + errorMessage);
    }
}
