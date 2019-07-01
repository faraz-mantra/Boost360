package com.nowfloats.RiaFCM;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.anachat.chatsdk.AnaCore;
import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.webengage.sdk.android.WebEngage;

/**
 * Created by NowFloats on 05-10-2016.
 */

public class RiaFirebaseInstanceIDService extends FirebaseMessagingService /*FirebaseInstanceIdService*/ {
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d("MyFirebaseIIDService", "onNewToken: " +token);
        saveTokenToPreferenceAndUpload(token);
        if (token != null) {
            AnaCore.saveFcmToken(this, token);
            WebEngage.get().setRegistrationID(FirebaseInstanceId.getInstance().getToken());
        }
    }

    /*@Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        saveTokenToPreferenceAndUpload(refreshedToken);
        if(refreshedToken != null) {
            AnaCore.saveFcmToken(this, refreshedToken);
            WebEngage.get().setRegistrationID(FirebaseInstanceId.getInstance().getToken());
        }
    }*/

    private void saveTokenToPreferenceAndUpload(String refreshedToken) {
        SharedPreferences pref = getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE);
        if (pref.getString("fpid", null) != null) {
            HomeActivity.registerChat(pref.getString("fpid", null));

        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
