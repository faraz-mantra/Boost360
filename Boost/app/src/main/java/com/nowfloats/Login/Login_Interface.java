package com.nowfloats.Login;

import com.nowfloats.Login.Model.Login_Data_Model;
import com.nowfloats.Login.Model.MessageModel;
import com.nowfloats.NavigationDrawer.Chat.ChatRegResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by guru on 26-05-2015.
 */
public interface Login_Interface {
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/Discover/v1/floatingPoint/verifyLogin")
    public void authenticationProcess(@Body HashMap<String,String> map, Callback<Login_Data_Model> callback);

    @Headers({"Content-Type: application/json"})
    @PUT("/Discover/v1/floatingpoint/notification/unregisterChannel")
    public void logoutUnsubcribeRIA(@Body HashMap<String,String> map,Callback<String> callback);

    @GET("/Discover/v1/floatingPoint/bizFloats")
    public void getMessages(@QueryMap Map<String,String> map, Callback<MessageModel> callback);

    @POST("/Discover/v1/floatingpoint/notification/registerChannel")
    public void post_RegisterRia(@Body HashMap<String,String> map, Callback<String> callback);

    //http://dbapi.fostergem.com
    @GET("/v1/saveDeviceId/{fpId}/{device_id}")
    public void chat(@Path("fpId") String fpId,@Path("device_id") String device_id, Callback<ChatRegResponse> callback);

    /*http://api.fostergem.com/v1/sendMessages/fpId/6c350c809e3acef7a38421b86c6619e3
Method : post
data : {"message":"hey","source":"merchant"}*/

    @POST("/v1/sendMessages/{fpId}/6c350c809e3acef7a38421b86c6619e3")
    public void sendChat(@Path("fpId") String fpId,@Body HashMap<String,String> map, Callback<ChatRegResponse> callback);

}
