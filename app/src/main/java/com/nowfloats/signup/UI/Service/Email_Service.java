package com.nowfloats.signup.UI.Service;

import android.app.Activity;
import android.util.Log;

import com.nowfloats.CustomWidget.MaterialProgressBar;
import com.nowfloats.signup.UI.API.Retro_Signup_Interface;
import com.nowfloats.signup.UI.Model.Email_Validation_Model;
import com.nowfloats.signup.UI.Model.ValidationEvent;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloatsDev on 13/05/2015.
 */
public class Email_Service {

    public Email_Service(final Activity activity, String email, String apiKey, final Bus bus) {
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("address", email);
            params.put("apikey", apiKey);
            Log.i("email verification data", "API call Started");
            // final Email_Validation_Model email_validation = "" ;
            Retro_Signup_Interface emailValidation = Constants.validEmailAdapter.create(Retro_Signup_Interface.class);
            emailValidation.get_IsValidEmail(params, new Callback<Email_Validation_Model>() {
                @Override
                public void success(Email_Validation_Model email_validation_models, Response response) {
                    bus.post(new ValidationEvent(email_validation_models));
                }

                @Override
                public void failure(RetrofitError error) {
                    MaterialProgressBar.dismissProgressBar();
                    //  bus.post(new ValidationEvent(email_validation));
                    Methods.showSnackBarNegative(activity, activity.getString(R.string.email_validation__failed));
                }
            });
        } catch (Exception e) {
            Log.i("EmailService data", "API Exception:" + e);
            e.printStackTrace();
        }
    }
}
