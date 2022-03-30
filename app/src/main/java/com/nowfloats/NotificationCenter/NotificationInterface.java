package com.nowfloats.NotificationCenter;

import com.nowfloats.NotificationCenter.Model.AlertModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;

/**
 * Created by guru on 19-06-2015.
 */
public interface NotificationInterface {

    @GET("/Discover/v1/floatingpoint/getnotifications")
    public void getAlerts(@QueryMap Map<String, String> map, Callback<ArrayList<AlertModel>> callback);


    @POST("/Discover/v1/floatingpoint/notification/notificationStatus")
    public void setRead(@Body JSONObject empty, @QueryMap Map<String, String> map, Callback<String> callback);


   @GET("/Discover/v1/floatingpoint/notificationscount")
    public void getAlertCount(@QueryMap Map<String, String> map, Callback<String> callback);


    @POST("/Discover/v1/floatingpoint/notification/changenotificationstatus")
    public void archiveAlert(@Body JSONObject empty, @QueryMap Map<String, String> map, Callback<String> callback);
}