package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;

import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloatsDev on 01/06/2015.
 */
public class Restricted_FP_Service {

    public Restricted_FP_Service(final Activity activity,String fpId, final HashMap<String, String> params,final Bus bus)
    {
        Restricted_FP_Interface restricted_fp_interface = Constants.restAdapter.create(Restricted_FP_Interface.class);
        restricted_fp_interface.get_RenewSubscriptionIsInterested(fpId,params,new Callback<String>() {
            @Override
            public void success(String s, Response response) {
               if(params.get("plantype").contains("WebsiteExpired"))
                   Methods.materialDialog(activity,"Thank you for your interest","Our team will reach out to you within the next 48 hours to tell you more our pricing plans.");
               else
                  Methods.materialDialog(activity,"Sorry to see you go :(","In case you are interested in the future, please reach out to us at "+activity.getResources().getString(R.string.settings_feedback_link));
            }

            @Override
            public void failure(RetrofitError error) {
                Methods.materialDialog(activity,"Oops","Something went wrong, please check back later");

            }
        });

    }
}
