package com.nowfloats.NavigationDrawer.API;

import com.nowfloats.NavigationDrawer.model.OfferModel;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.QueryMap;

/**
 * Created by NowFloats on 04-11-2016.
 */

public interface OffersInterface {
    @GET("/Discover/v3/floatingPoint/latestOffers")
    @FormUrlEncoded
    public void getAllOffers(@QueryMap Map<String,String> map, Callback<OfferModel> callback);

    @Headers({"Content-Type: application/json"})
    @PUT("/Discover/v1/float/createDeal")
    public void postOffer(@Body HashMap<String,String> map, Callback<String> callback);
}
