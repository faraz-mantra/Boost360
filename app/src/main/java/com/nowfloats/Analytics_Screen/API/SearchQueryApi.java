package com.nowfloats.Analytics_Screen.API;

import com.google.gson.JsonObject;
import com.nowfloats.Analytics_Screen.model.AnalyticsResponse;
import com.nowfloats.Analytics_Screen.model.SearchAnalyticsSummaryForFP;
import com.nowfloats.Analytics_Screen.model.SearchQueryModel;
import com.nowfloats.Analytics_Screen.model.SearchRankModel;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Admin on 09-03-2017.
 */

public interface SearchQueryApi {

    @POST("/Search/v1/queries/report")
    void getQueries(@Query("offset") String offset, @Body JsonObject obj, Callback<List<SearchQueryModel>> response);

    @GET("/market/api/KeywordRank/KeywordRankFinder")
    void getKeyWordRanks(@Query("fpTag") String fpTag, Callback<List<SearchRankModel>> response);

    @GET("/market/api/SearchAnalytics/GetSearchAnalyticsSummaryForFP")
    void GetSearchAnalyticsSummaryForFP(@Query("fpTag") String fpTag, Callback<SearchAnalyticsSummaryForFP> response);


    /**
     * New Analytics Api
     */
    @POST("/GetSearchAnalytics")
    void getSearchQueries(@Body Map<String, Object> map, Callback<List<AnalyticsResponse>> response);
}
