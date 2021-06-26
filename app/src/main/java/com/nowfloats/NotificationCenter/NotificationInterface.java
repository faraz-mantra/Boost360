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
    //https://api.withfloats.com/Discover/v1/floatingpoint/getnotifications?
    // clientId=1YGC764N4AV65XJ9ELXIGJAZ9W3P4K28UUIFX8ZUMW0VTTOJ2EZR9T1KVI6A79RE&
    // fpId=5406bd254ec0a40d409f2b2b&isRead=&&offset=0&limit=10
    @GET("/Discover/v1/floatingpoint/getnotifications")
    public void getAlerts(@QueryMap Map<String, String> map, Callback<ArrayList<AlertModel>> callback);


    //https://api.withfloats.com/Discover/v1/floatingpoint/notification/notificationStatus?
    // fpId=5406bd254ec0a40d409f2b2b&clientId=1YGC764N4AV65XJ9ELXIGJAZ9W3P4K28UUIFX8ZUMW0VTTOJ2EZR9T1KVI6A79RE&notificationId=558415374ec0a42df8ca0937&isRead=FALSE
    @POST("/Discover/v1/floatingpoint/notification/notificationStatus")
    public void setRead(@Body JSONObject empty, @QueryMap Map<String, String> map, Callback<String> callback);


    //https://api.withfloats.com/Discover/v1/floatingpoint/notificationscount?
    // clientId=1YGC764N4AV65XJ9ELXIGJAZ9W3P4K28UUIFX8ZUMW0VTTOJ2EZR9T1KVI6A79RE&fpId=5406bd254ec0a40d409f2b2b&isRead=false
    @GET("/Discover/v1/floatingpoint/notificationscount")
    public void getAlertCount(@QueryMap Map<String, String> map, Callback<String> callback);


    @POST("/Discover/v1/floatingpoint/notification/changenotificationstatus")
    public void archiveAlert(@Body JSONObject empty, @QueryMap Map<String, String> map, Callback<String> callback);
}