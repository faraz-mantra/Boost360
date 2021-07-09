package com.nowfloats.NavigationDrawer.API;

import com.nowfloats.NavigationDrawer.model.FacebookWildFireDataModel;
import com.nowfloats.NavigationDrawer.model.WildFireDataModel;
import com.nowfloats.NavigationDrawer.model.WildFireKeyStatsModel;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by Admin on 30-11-2017.
 */

public interface WildFireApis {
    String WILD_FIRE_END_POINT = "http://wmt.withfloats.com/wildfire/api";
    RestAdapter adapter = new RestAdapter.Builder().setEndpoint(WILD_FIRE_END_POINT)/*.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg"))*/.build();

    @GET("/v1/account/keywordstats")// google keywords data
    void getGoogleStats(@QueryMap Map map, Callback<ArrayList<WildFireKeyStatsModel>> response);

    //@GET("/WildFire/v1/account/detailswithexternalsourceid/{sourceId}")
    @GET("/v1/account/detailswithexternalsourceid/{sourceId}")
    void getWildFireData(@Path("sourceId") String sourceId, @Query("clientId") String clientId, Callback<WildFireDataModel> response);

    @GET("/v1/account/wildfirechanneltype")// ads enabled channel types
    void getWildFireChannels(@Query("clientId") String clientId, @Query("wfId") String accountId, Callback<ArrayList<String>> response);

    @GET("/v2/account/facebookadStats") // facebook keywords data
    void getFacebookStats(@QueryMap Map map, Callback<ArrayList<FacebookWildFireDataModel>> response);
}
