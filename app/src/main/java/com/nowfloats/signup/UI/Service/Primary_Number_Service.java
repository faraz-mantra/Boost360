package com.nowfloats.signup.UI.Service;

import android.app.Activity;

import com.nowfloats.signup.UI.API.Retro_Signup_Interface;
import com.nowfloats.signup.UI.Model.Primary_Number_Event;
import com.nowfloats.util.Constants;
import com.squareup.otto.Bus;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloatsDev on 24/05/2015.
 */
public class Primary_Number_Service {

    public Primary_Number_Service(final Activity activity, String clientID, String primaryNumber, final Bus bus) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("clientId", clientID);
        params.put("mobile", primaryNumber);

        Retro_Signup_Interface primaryNumberValidation = Constants.restAdapter.create(Retro_Signup_Interface.class);

        primaryNumberValidation.post_verifyUniqueNumber(params, new Callback<Boolean>() {

            @Override
            public void success(Boolean aBoolean, Response response) {
                bus.post(new Primary_Number_Event(aBoolean));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
