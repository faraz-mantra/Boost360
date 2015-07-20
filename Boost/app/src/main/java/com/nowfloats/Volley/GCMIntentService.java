package com.nowfloats.Volley;

/**
 * Created by NowFloatsDev on 29/04/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.demach.konotor.Konotor;
import com.demach.konotor.access.K;
import com.google.android.gcm.GCMBaseIntentService;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.util.Constants;

public class GCMIntentService extends GCMBaseIntentService
{
    public static final String TAG = GCMIntentService.class.getName();

    public GCMIntentService()
    {
        super(K.ANDROID_PROJECT_SENDER_ID);
    }

    @Override
    protected void onMessage(Context context, Intent intent)
    {
        Log.i("","Push NOtif came.....");
        if(intent!=null){
            Konotor.getInstance(context).handleGcmOnMessage(intent);
        }
    }

    @Override
    protected void onError(Context context, String errorId)
    {
        Konotor.getInstance(context).handleGcmOnError(errorId);
    }

    @Override
    protected void onRegistered(Context context, String registrationId)
    {
        Log.d("GCMIntentService","Registration Id : "+registrationId);
        Constants.gcmRegistrationID = registrationId ;
//        Konotor.getInstance(getApplicationContext()).updateGcmRegistrationId(Constants.gcmRegistrationID);
        Konotor.getInstance(context).handleGcmOnRegistered(registrationId);
        HomeActivity.setGCMId(Constants.gcmRegistrationID);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId)
    {
        Konotor.getInstance(context).handleGcmOnUnRegistered(registrationId);
    }
}
