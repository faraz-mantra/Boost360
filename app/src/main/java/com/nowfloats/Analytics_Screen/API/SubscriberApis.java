package com.nowfloats.Analytics_Screen.API;

import com.nowfloats.Analytics_Screen.model.AddSubscriberModel;
import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.Analytics_Screen.model.UnsubscriberModel;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Admin on 02-03-2017.
 */

public interface SubscriberApis {
        @GET("/Discover/v1/FloatingPoint/{FPTAG}/subscribers/{CLIENTID}")
        void getsubscribers(@Path("FPTAG") String fpTag, @Path("CLIENTID") String clientId, @Query("offset") String offset, Callback<List<SubscriberModel>> response);

        @POST("/Discover/v1/FloatingPoint/subscribe")
        void addSubscriber(@Body AddSubscriberModel model, Callback<String> response);

        @POST("/Search/v1/floatingPoint/Subscribers/Search")
        void search(@Body String dummy, @Query("email") String key, @Query("clientId") String clientId, @Query("fpTag") String fpTag,Callback<ArrayList<SubscriberModel>> response);

        @POST("/Discover/v1/FloatingPoint/unsubscribe")
        void unsubscriber(@Body UnsubscriberModel model, Callback<Object> response);
}
