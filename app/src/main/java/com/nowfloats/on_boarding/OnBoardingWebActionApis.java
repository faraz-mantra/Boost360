package com.nowfloats.on_boarding;

import com.boost.presignin.model.userprofile.ConnectUserProfileResponse;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.on_boarding.models.OnBoardingAddModel;
import com.nowfloats.on_boarding.models.OnBoardingDataModel;
import com.nowfloats.on_boarding.models.OnBoardingUpdateModel;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Admin on 22-03-2018.
 */

public interface OnBoardingWebActionApis {
    public static final String ON_BOARDING_KEY = "58ede4d4ee786c1604f6c535";

    @GET("/fp_onboarding/get-data")
    @Headers({"Authorization: " + ON_BOARDING_KEY})
    void getData(@Query("query") String query, Callback<WebActionModel<OnBoardingDataModel>> callback);

    @POST("/fp_onboarding/add-data")
    @Headers({"Authorization: " + ON_BOARDING_KEY})
    void addData(@Body OnBoardingAddModel model, Callback<String> callback);

    @POST("/fp_onboarding/update-data")
    @Headers({"Authorization: " + ON_BOARDING_KEY})
    void updateData(@Body OnBoardingUpdateModel model, Callback<String> callback);

    @POST("/user/v9/floatingPoint/MerchantProfileStatus")
    void getMerchantProfieStatus(@Query("clientId") String clientId, @Body String[] fpIds, Callback<ConnectUserProfileResponse> connectUserProfileResponseCallback);

}
