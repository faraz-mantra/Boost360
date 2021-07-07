package com.nowfloats.signup.UI.Service;

import android.app.Activity;

import com.nowfloats.signup.UI.API.Retro_Signup_Interface;
import com.nowfloats.signup.UI.Model.Suggest_Tag_Event;
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
public class Suggest_Tag_Service {

    public Suggest_Tag_Service(final Activity activity, String name, String city, String country, String category, String cliendId, final Bus bus) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("city", city);
        params.put("country", country);
        params.put("category", category);
        params.put("clientId", cliendId);

        Retro_Signup_Interface suggestTag = Constants.restAdapter.create(Retro_Signup_Interface.class);
        suggestTag.post_SuggestTag(params, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if (s != null && s.toString().trim().length() > 0)
                    bus.post(new Suggest_Tag_Event(s));
                else
                    Methods.showSnackBarNegative(activity, activity.getString(R.string.suggest_tag_failed_try_again));
            }

            @Override
            public void failure(RetrofitError error) {
                Methods.showSnackBarNegative(activity, activity.getString(R.string.suggest_tag_failed_try_again));
            }
        });

    }
}