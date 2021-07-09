package com.nowfloats.Login;

import android.app.Activity;
import android.util.Log;

import com.nowfloats.util.Constants;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloatsDev on 11/06/2015.
 */
public class Ria_Register {

    public Ria_Register(final Activity activity,String clientID,String deviceType,String channel)
    {
        try {
            UserSessionManager session = new UserSessionManager(activity.getApplicationContext(),activity);
            String userId = session.getFPID();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("clientId", clientID);
            params.put("DeviceType", deviceType);
            params.put("Channel", channel);
            params.put("UserId", userId);
            Log.i("Ria_Register GCM id--", "API call Started");

            Login_Interface emailValidation = Constants.restAdapter.create(Login_Interface.class);
            emailValidation.post_RegisterRia(params,new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.i("GCM local ","reg success");
                    Log.d("Response","Response : "+s);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("GCM local ","reg FAILed");
                }
            });
        } catch (Exception e) {
            Log.i("Ria_Register ", "API Exception:" + e);
            e.printStackTrace();
        }
    }
}
