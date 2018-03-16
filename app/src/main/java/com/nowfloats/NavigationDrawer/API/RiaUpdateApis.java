package com.nowfloats.NavigationDrawer.API;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Admin on 07-12-2017.
 */

public interface RiaUpdateApis {

    @GET("/market/api/KeywordRank/Getkeywordsuggestionforfp")
    void getUpdateKeywordSuggestions(@Query("fpTag") String fpTag, Callback<ArrayList<String>> response);
}
