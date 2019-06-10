package com.nowfloats.Image_Gallery;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ImageApi {

    @GET("/discover/v1/floatingpoint/getBackgroundImages")
    void getBackgroundImages(@Query("fpId") String fpId, @Query("clientId") String clientId, Callback<List<String>> response);
}