package com.nowfloats.Analytics_Screen.API;

import com.nowfloats.Analytics_Screen.model.SearchQueryModel;

import org.json.JSONObject;

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
    void getQueries(@Query("offset") String offset, @Body JSONObject obj, Callback<List<SearchQueryModel>> response);
}
