package com.nowfloats.Analytics_Screen.API;

import com.nowfloats.Analytics_Screen.model.SubscriberModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Admin on 02-03-2017.
 */

public interface SubscriberApis {
        @GET("/Discover/v1/FloatingPoint/{FPTAG}/subscribers/{CLIENTID}")
        void getsubscribers(@Path("FPTAG") String fpTag, @Path("CLIENTID") String clientId, @Query("offset") String offset, Callback<List<SubscriberModel>> response);
}
