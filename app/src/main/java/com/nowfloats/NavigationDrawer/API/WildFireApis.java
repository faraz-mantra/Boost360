package com.nowfloats.NavigationDrawer.API;

import com.nowfloats.NavigationDrawer.model.WildFireDataModel;
import com.nowfloats.NavigationDrawer.model.WildFireKeyStatsModel;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Admin on 30-11-2017.
 */

public interface WildFireApis {
    String WILD_FIRE_END_POINT = "http://wmt.withfloats.com/wildfire/api";
    RestAdapter adapter = new RestAdapter.Builder().setEndpoint(WILD_FIRE_END_POINT).build();
    @GET("/v1/account/keywordstats")
    void getGoogleStats(@Query("clientId") String clientId, @Query("accountId") String accountId, Callback<ArrayList<WildFireKeyStatsModel>> response);

    @GET("/WildFire/v1/account/detailswithexternalsourceid/{sourceId}")
    void getWildFireData(@Path("sourceId") String sourceId, @Query("clientId") String clientId, Callback<WildFireDataModel> response);

    @GET("/v1/account/wildfirechanneltype")
    void getWildFireChannels(@Query("clientId") String clientId, @Query("wfId") String accountId, Callback<ArrayList<String>> response);
    @GET("/v2/account/facebookadStats")
    void getFacebookStats(@Query("clientId") String clientId, @Query("accountId") String accountId, Callback<ArrayList<WildFireKeyStatsModel>> response);
}
