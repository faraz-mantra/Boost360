package com.nowfloats.Analytics_Screen.API;

import com.google.gson.JsonObject;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by Admin on 27-04-2017.
 */

public interface CallTrackerApis {
    @GET("/Wildfire/v1/calls/tracker")
    void trackerCalls(@QueryMap Map data, Callback<ArrayList<VmnCallModel>> response);

    @GET("/Wildfire/v1/calls/GetLastCallLogInfoWithRange")
    void getLastCallInfo(@QueryMap Map data, Callback<ArrayList<VmnCallModel>> response);

    @GET("/WildFire/v1/calls/summary")
    void getVmnSummary(@Query("clientId") String clientId, @Query("fpid") String fpId, @Query("identifierType") String type, Callback<JsonObject> response);

    @GET("/memory/api/fpactivity/countfpactivity")
    void getCallCountByType(@Query("fptag") String fptag,@Query("eventType") String eventType,@Query("eventChannel") String eventChannel, Callback<JsonObject> response);

    @POST("/api/Service/EmailRIASupportTeamV2")
    void requestVmn(@Body Map<String,String> bodyMap, @Query("authClientId") String clientId, @Query("fpTag") String fpTag, Callback<Boolean> response);
}
