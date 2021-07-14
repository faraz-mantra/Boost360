package com.nowfloats.signup.UI.Service;

import android.util.Log;
import android.widget.Toast;

import com.nowfloats.signup.UI.API.Retro_Signup_Interface;
import com.nowfloats.signup.UI.Model.Create_Store_Event;
import com.nowfloats.signup.UI.UI.PreSignUpActivityRia;
import com.nowfloats.signup.UI.UI.WebSiteAddressActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloatsDev on 25/05/2015.
 */
public class Create_Tag_Service {

    public Create_Tag_Service(final WebSiteAddressActivity activity, HashMap<String, String> jsonObject, final Bus bus) {
        Retro_Signup_Interface createStore = Constants.restAdapter.create(Retro_Signup_Interface.class);

        createStore.put_createStore(jsonObject, "jhghghgjghghjghghgj", new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                bus.post(new Create_Store_Event(s));
            }

            @Override
            public void failure(RetrofitError error) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                        if (activity.pd != null) {
                            activity.pd.dismiss();
                        }
                    }
                });
            }
        });


    }

    public Create_Tag_Service(final PreSignUpActivityRia activity, String existingProfileId, HashMap<String, String> jsonObject, final Bus bus) {
        Retro_Signup_Interface createStore = Constants.restAdapter.create(Retro_Signup_Interface.class);

        createStore.put_createStore(jsonObject, existingProfileId, new Callback<String>() {
            @Override
            public void success(String s, Response response) {

                bus.post(new Create_Store_Event(s));
            }

            @Override
            public void failure(final RetrofitError error) {

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Methods.showSnackBarNegative(activity, String.valueOf(error.getBody()));

                        if (activity.pd != null) {
                            activity.pd.dismiss();
                        }
                    }
                });

                bus.post(new Create_Store_Event(null));
            }
        });
    }
}
