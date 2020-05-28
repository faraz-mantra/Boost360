package com.nowfloats.Image_Gallery;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface ImageApi {

    @GET("/discover/v1/floatingpoint/getBackgroundImages")
    void getBackgroundImages(@Query("fpId") String fpId, @Query("clientId") String clientId, Callback<List<String>> response);

    @POST("/discover/v1/floatingpoint/backgroundImage/delete")
    void deleteBackgroundImages(@Body Map<String, String> map, Callback<Object> response);
}