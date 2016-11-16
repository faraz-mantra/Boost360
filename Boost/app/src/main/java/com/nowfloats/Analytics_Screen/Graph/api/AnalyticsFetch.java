package com.nowfloats.Analytics_Screen.Graph.api;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import com.nowfloats.Analytics_Screen.Graph.model.DashboardResponse;

/**
 * Created by Abhi on 11/8/2016.
 */

public class AnalyticsFetch {
    public interface FetchDetails{
        @GET("/Dashboard/v1/{FPTAG}/details?detailstype=0&scope=0")
        void getDataCount(@Path("FPTAG") String tag, @QueryMap Map<String, String> data, Callback<DashboardResponse> response);
    }

}