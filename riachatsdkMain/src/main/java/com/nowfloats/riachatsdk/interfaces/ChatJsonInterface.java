package com.nowfloats.riachatsdk.interfaces;

import com.nowfloats.riachatsdk.models.RiaCardModel;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by NowFloats on 27-03-2017.
 */

public interface ChatJsonInterface {
    @GET("/chat/api/Conversation/Chat")
    void getChatJson(@QueryMap Map<String, String> query, Callback<List<RiaCardModel>> nodeList);
}
