package com.nowfloats.Volley;

/**
 * Created by NowFloatsDev on 05/05/2015.
 */
import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMBroadcastReceiver;

import static

        com.google.android.gcm.GCMConstants.DEFAULT_INTENT_SERVICE_CLASS_NAME;

public class AndroidGCMBroadcastReceiver extends GCMBroadcastReceiver {

    @Override
    protected String getGCMIntentServiceClassName(Context context) {

        Log.d("GCMIntentService ","Intent_Service"+DEFAULT_INTENT_SERVICE_CLASS_NAME);

        return "com.nowfloats.Volley" + DEFAULT_INTENT_SERVICE_CLASS_NAME;

    }

}
