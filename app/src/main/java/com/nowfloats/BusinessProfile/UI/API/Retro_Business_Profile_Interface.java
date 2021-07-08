package com.nowfloats.BusinessProfile.UI.API;

import com.nowfloats.BusinessProfile.UI.Model.ContactInformationUpdateModel;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by NowFloatsDev on 26/05/2015.
 */
public interface Retro_Business_Profile_Interface {


    //https://api.withfloats.com/Discover/v1/floatingpoint/update/

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/Discover/v1/floatingpoint/update/")
    void post_updateBusinessDetails(@Body JSONObject jsonObject, Callback<ArrayList<String>> callback);


    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/Discover/v1/floatingpoint/update/")
    void updateContactInformation(@Body ContactInformationUpdateModel body, Callback<ArrayList<String>> callback);


//    @Multipart
//    @Headers({"Content-Type: application/json","Accept: application/json"})
//    @POST("/Discover/v1/floatingpoint/update/")
//    void post_updateBusinessDetails(@Part("map") HashMap<String,String> map,@Part("data") HashMap<String,ArrayList> data, Callback<ArrayList<String>> callback);


}
