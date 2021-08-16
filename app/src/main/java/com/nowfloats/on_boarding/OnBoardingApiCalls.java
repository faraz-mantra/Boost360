package com.nowfloats.on_boarding;

import android.util.Log;

import com.nowfloats.on_boarding.models.OnBoardingAddModel;
import com.nowfloats.on_boarding.models.OnBoardingStepsModel;
import com.nowfloats.on_boarding.models.OnBoardingUpdateModel;
import com.nowfloats.util.Constants;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 22-03-2018.
 */

public class OnBoardingApiCalls {

    public static void updateData(String fptag, String value) {
        OnBoardingWebActionApis apis = Constants.webActionAdapter.create(OnBoardingWebActionApis.class);
        OnBoardingUpdateModel model = new OnBoardingUpdateModel();
        model.setQuery(String.format("{fptag:'%s'}", fptag));
        model.setUpdateValue(String.format("{$set:{%s}}", value));
        apis.updateData(model, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                //Log.v("ggg",s);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("ggg", error.getMessage());
            }
        });
    }

    public static void addData(OnBoardingStepsModel boardingStepsModel) {

        OnBoardingAddModel model = new OnBoardingAddModel();
        model.setActionData(boardingStepsModel);
        model.setWebsiteId(OnBoardingWebActionApis.ON_BOARDING_KEY);

        OnBoardingWebActionApis apis = Constants.webActionAdapter.create(OnBoardingWebActionApis.class);
        apis.addData(model, new Callback<String>() {
            @Override
            public void success(String s, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
