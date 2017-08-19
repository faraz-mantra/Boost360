package com.nowfloats.sam.service;

import com.nowfloats.sam.models.MessageDO;
import com.nowfloats.sam.models.SMSSuggestions;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by NowFloats on 04-11-2016.
 */

public interface SuggestionsInterface {

    @Headers({"Content-Type: application/json"})
    @POST("/processSMSdata")
    public void processSMSData(@Body Map<String, String> bodyMap, Callback<SMSSuggestions> callback);

    @Headers({"Content-Type: application/json"})
    @POST("/saveCallData")
    public void saveCallData(@Body List<MessageDO> messages, Callback<String> callback);

    @Headers({"Content-Type: application/json"})
    @POST("/storeUserRating")
    public void updateRating(@Body Map<String, String> bodyMap, Callback<String> callback);

}
