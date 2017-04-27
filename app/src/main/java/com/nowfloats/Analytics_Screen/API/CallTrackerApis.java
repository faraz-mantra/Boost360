package com.nowfloats.Analytics_Screen.API;

import com.nowfloats.Analytics_Screen.model.VmnCallModel;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by Admin on 27-04-2017.
 */

public interface CallTrackerApis {
    @GET("/Wildfire/v1/calls/GetLastCallLogInfoWithRange")
    void getLastCallInfo(@QueryMap Map data, Callback<List<VmnCallModel>> response);

    @GET("/Wildfire/v1/calls/totalcount")
    void getTotalCalls(@Query("clientId") String clientId, @Query("fpid") String fpId, Callback<String> response);
}
