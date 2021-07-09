package com.nowfloats.signup.UI.Service;

import android.app.Activity;
import android.util.Log;

import com.nowfloats.signup.UI.API.Facebook_Interface;
import com.squareup.otto.Bus;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloatsDev on 08/06/2015.
 */
public class Facebook_Service {

    public static final RestAdapter facebookAdapter = new RestAdapter.Builder().setEndpoint("https://graph.facebook.com").build();


    public Facebook_Service(final Activity activity,String accessToken,final Bus bus)
    {
        Facebook_Interface getFacebookDetails = facebookAdapter.create(Facebook_Interface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", accessToken);
        getFacebookDetails.get_Me_Details(params,new Callback<JSONObject>() {
            @Override
            public void success(JSONObject s, Response response) {

                Log.d("Response","Response : "+s);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
