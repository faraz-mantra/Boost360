package com.nowfloats.signup.UI.API;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by Dell on 10-01-2015.
 */
public interface API_Layer {

    @GET("/Discover/v1/floatingPoint/categories")
    void getCategories(@QueryMap Map<String, String> map, Callback<ArrayList<String>> response);

}
