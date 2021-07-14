package com.nowfloats.BusinessProfile.UI.API;

import android.app.Activity;

import com.nowfloats.util.Constants;
import com.squareup.otto.Bus;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloatsDev on 26/05/2015.
 */
public class Business_Info_Upload_Service {


    public Business_Info_Upload_Service(Activity activity, JSONObject jsonObject, final Bus bus) {
        Retro_Business_Profile_Interface uploadInterface = Constants.restAdapter.create(Retro_Business_Profile_Interface.class);


        uploadInterface.post_updateBusinessDetails(jsonObject, new Callback<ArrayList<String>>() {
            @Override
            public void success(ArrayList<String> strings, Response response) {
                bus.post(strings);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
