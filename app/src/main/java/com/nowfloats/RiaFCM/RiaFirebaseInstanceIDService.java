package com.nowfloats.RiaFCM;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.freshdesk.hotline.Hotline;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nowfloats.NavigationDrawer.HomeActivity;

/**
 * Created by NowFloats on 05-10-2016.
 */

public class RiaFirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        saveTokenToPreferenceAndUpload(refreshedToken);
        if(refreshedToken != null) {
            Hotline.getInstance(this).updateGcmRegistrationToken(refreshedToken);
        }
    }

    private void saveTokenToPreferenceAndUpload(String refreshedToken) {
        SharedPreferences pref = getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE);
        if(pref.getString("fpid", null)!=null){
            HomeActivity.registerChat(pref.getString("fpid", null));

        }
    }
}
