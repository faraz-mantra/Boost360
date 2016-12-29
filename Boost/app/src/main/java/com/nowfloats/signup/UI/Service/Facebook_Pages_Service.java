package com.nowfloats.signup.UI.Service;

import android.app.Activity;
import android.util.Log;

import com.nowfloats.signup.UI.API.Facebook_Interface;
import com.nowfloats.signup.UI.Model.Facebook_Data_Model;
import com.nowfloats.signup.UI.Model.Facebook_Event;
import com.squareup.otto.Bus;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloatsDev on 08/06/2015.
 */
public class Facebook_Pages_Service {
    public static final RestAdapter facebookAdapter = new RestAdapter.Builder().setEndpoint("https://graph.facebook.com").build();
    public Facebook_Pages_Service(final Activity activity,String accessToken,String pageID,final Bus bus)
    {
        Facebook_Interface getFacebookDetails = facebookAdapter.create(Facebook_Interface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", accessToken);
        getFacebookDetails.get_Me_Accounts_Details(pageID,params, new Callback<Facebook_Data_Model>() {
            @Override
            public void success(Facebook_Data_Model s, Response response) {
                bus.post(new Facebook_Event(s));
                Log.d("Response", "Response : " + s);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}