package com.nowfloats.SiteAppearance;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Admin on 28-06-2017.
 */

public interface ThemeApis {
    @POST("/Kitsune/v1/fixtheme")
    void setTheme(@Query("clientId") String clientId, @Query("fpTag") String fpTag, @Query("templateId") String templateId,Callback<String> response);

    @POST("/Kitsune/v1/unfixtheme")
    void setDynamicTheme(@Query("clientId") String clientId, @Query("fpTag") String fpTag,Callback<String> response);

}
