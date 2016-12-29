package com.nowfloats.signup.UI.Service;

import android.app.Activity;

import com.nowfloats.signup.UI.API.Retro_Signup_Interface;
import com.nowfloats.signup.UI.Model.Verifty_Unique_Tag_Event;
import com.nowfloats.util.Constants;
import com.squareup.otto.Bus;

import java.util.HashMap;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloatsDev on 25/05/2015.
 */
public class Verify_Tag_Service {

    public Verify_Tag_Service(Activity activity,String fpTag,String fpName,String clientId,final Bus bus)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("fpTag", fpTag);
        params.put("fpName", fpName);
        params.put("clientId", clientId);

        Retro_Signup_Interface verifyTag = Constants.restAdapter.create(Retro_Signup_Interface.class);

        verifyTag.post_verifyTag(params,new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                bus.post(new Verifty_Unique_Tag_Event(s));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

}
