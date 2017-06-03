package com.nowfloats.signup.UI.API;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Dell on 10-01-2015.
 */
public interface API_Layer {

    @GET("/Discover/v1/floatingPoint/categories")
    void getCategories(Callback<ArrayList<String>> response);

}
