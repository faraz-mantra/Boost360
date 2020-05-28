package com.nowfloats.BusinessProfile.UI.API;

//import com.android.volley.toolbox.Volley.AppController;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Dell on 16-01-2015.
 */
public interface UpdatePrimaryNumApi {

    @POST("/Discover/v1/floatingPoint/UpdateFloatingPointPrimaryNumber")
    void changeNumber(@Query("fpId") String fpId, @Query("clientId") String clientId, @Body String number, Callback<String> response);
}