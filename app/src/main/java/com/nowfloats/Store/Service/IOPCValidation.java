package com.nowfloats.Store.Service;

import com.nowfloats.Store.Model.MainMailModel;
import com.nowfloats.Store.Model.MarkAsPaidModel;
import com.nowfloats.Store.Model.OPCModels.OPCDataMain;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by NowFloats on 09-11-2016.
 */

public interface IOPCValidation {
    @GET("/Internal/v1/VerifyPaymentToken")
    public void verifyPaymentToken(@QueryMap Map<String,String> map, Callback<OPCDataMain> callback);

    @GET("/Internal/v1/RedeemPaymentToken")
    public void redeemToken(@QueryMap Map<String,String> map, Callback<String> callback);

    @POST("/support/v1/MarkFloatingPointAsPaid")
    public void markAsPaid(@Body MarkAsPaidModel model, Callback<String> callback);

    @POST("/Internal/v1/PushEmailToQueue/{CLIEND_ID_FOR_VALIDATION}")
    public void sendMail(@Path("CLIEND_ID_FOR_VALIDATION") String clientId, @Body MainMailModel mailData, Callback<String> callback);

}
