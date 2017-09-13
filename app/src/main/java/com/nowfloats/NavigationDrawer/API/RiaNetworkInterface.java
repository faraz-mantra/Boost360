package com.nowfloats.NavigationDrawer.API;

import com.nowfloats.NavigationDrawer.model.RiaCardModel;
import com.nowfloats.NavigationDrawer.model.RiaSupportModel;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.QueryMap;

/**
 * Created by NowFloats on 02-01-2017.
 */

public interface RiaNetworkInterface {
    @Headers({"Content-Type: application/json"})
    @GET("/api/RIASupportTeam/GetMemberForFP")
    public void getMemberForFp(@QueryMap Map<String, String> map, Callback<RiaSupportModel> callback);

    @GET("/riacards/api/RiaCard/GetRiaCards")
    public void getRiaCards(@QueryMap Map<String, String> map,  Callback<ArrayList<RiaCardModel>> callback);
}
