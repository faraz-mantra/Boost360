package com.nowfloats.NavigationDrawer.API;

import com.nowfloats.NavigationDrawer.model.VisitAnalytics;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.QueryMap;

public interface VisitorsApiInterface {

    @POST("/dashboard/v2/visitsAnalytics")
    void getVisitors(@Body List<String> websiteIds, @QueryMap Map<String, String> map, Callback<List<VisitAnalytics>> callback);
}