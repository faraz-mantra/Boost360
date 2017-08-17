package com.nowfloats.managecustomers.apis;

import com.google.gson.JsonObject;
import com.nowfloats.managecustomers.models.FacebookChatDataModel;
import com.nowfloats.managecustomers.models.FacebookMessageModel;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Admin on 17-08-2017.
 */

public class FacebookChatApis {

    static String ENDPOINT = "https://5d8dab45.ngrok.io";
    static final RestAdapter adapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
    public FacebookApis getFacebookChatApis(){
        return adapter.create(FacebookApis.class);
    }
    public interface FacebookApis{
        @GET("/api/messages")
        void getAllMessages(@Query("identifier") String identifier, @Query("nowfloats_id") String fpId, Callback<FacebookChatDataModel> response);

        @POST("/api/messages")
        void sendMessageToUser(@Body FacebookMessageModel model, Callback<JsonObject> response);
    }
}
