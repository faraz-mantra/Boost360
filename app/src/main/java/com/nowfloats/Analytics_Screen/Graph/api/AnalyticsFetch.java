package com.nowfloats.Analytics_Screen.Graph.api;

import com.nowfloats.Analytics_Screen.Graph.model.DashboardResponse;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by Abhi on 11/8/2016.
 */

public class AnalyticsFetch {
    public interface FetchDetails{
        @GET("/Dashboard/v1/{FPTAG}/details")
        void getDataCount(@Path("FPTAG") String tag, @QueryMap Map<String, String> data, Callback<DashboardResponse> response);
    }

}