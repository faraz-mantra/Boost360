package com.nowfloats.Analytics_Screen.API;

import com.google.gson.JsonObject;
import com.nowfloats.Analytics_Screen.model.SearchQueryModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Admin on 09-03-2017.
 */

public interface SearchQueryApi {

    @POST("/Search/v1/queries/report")
    void getQueries(@Query("offset") String offset, @Body JsonObject obj, Callback<List<SearchQueryModel>> response);
}
