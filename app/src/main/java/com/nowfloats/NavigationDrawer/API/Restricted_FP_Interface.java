package com.nowfloats.NavigationDrawer.API;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by NowFloatsDev on 01/06/2015.
 */
public interface Restricted_FP_Interface {

    ///Discover/v1/floatingPoint/
    // fpID/
    // requestplan?
    // clientId=ClientID&
    // plantype="Expiry Text"

    @GET("/Discover/v1/floatingPoint/{fpid}/requestplan?")
    void get_RenewSubscriptionIsInterested(
            @Path("fpid") String fpid,
            @QueryMap Map<String,String> map, Callback<String> callback);
}
