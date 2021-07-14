package com.nowfloats.Login;

import com.nowfloats.Login.Model.Login_Data_Model;
import com.nowfloats.Login.Model.MessageModel;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.QueryMap;

/**
 * Created by guru on 26-05-2015.
 */
public interface Login_Interface {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/Discover/v1/floatingPoint/verifyLogin")
    public void authenticationProcess(@Body HashMap<String, String> map, Callback<Login_Data_Model> callback);

    @Headers({"Content-Type: application/json"})
    @PUT("/Discover/v1/floatingpoint/notification/unregisterChannel")
    public void logoutUnsubcribeRIA(@Body HashMap<String, String> map, Callback<String> callback);

    @GET("/Discover/v1/floatingPoint/bizFloats")
    public void getMessages(@QueryMap Map<String, String> map, Callback<MessageModel> callback);

    //Get new Available Updates
    @GET("/Discover/v1/floatingPoint/GetLatestBizFloatsFromMessage")
    public void getNewAvailableMessage(@QueryMap Map<String, String> map, Callback<MessageModel> callback);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/Discover/v1/floatingpoint/notification/registerChannel")
    public void post_RegisterRia(@Body HashMap<String, String> map, Callback<String> callback);
}
