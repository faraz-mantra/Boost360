package com.nowfloats.RiaFCM;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nowfloats.Login.Login_Interface;
import com.nowfloats.NavigationDrawer.Chat.ChatRegResponse;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
        BoostLog.d(TAG, "Refreshed token: " + refreshedToken);
        saveTokenToPreferenceAndUpload(refreshedToken);

    }

    private void saveTokenToPreferenceAndUpload(String refreshedToken) {
        SharedPreferences pref = getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE);
        if(pref.getString("fpid", null)!=null){
            HomeActivity.registerChat(pref.getString("fpid", null));
        }
    }
}
