package com.nowfloats.NavigationDrawer.API;

import com.google.gson.JsonObject;
import com.nowfloats.NavigationDrawer.model.StoreAndGoModel;
import com.nowfloats.util.Constants;

import java.util.List;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Admin on 1/4/2017.
 */

public class BusinessAppApis {

    public static AppApis getRestAdapter() {
        return Constants.restAdapter.create(AppApis.class);
    }

    public interface AppApis {

        @POST("/Discover/v1/floatingpoint/getAPKstatus")
        void getStatus(@Query("clientId") String clientId, @Query("fpId") String fpId, Callback<JsonObject> response);

        @POST("/Discover/v1/floatingpoint/downloadAPK")
        void getDownload(@Query("clientId") String clientId, @Query("fpId") String fpId, Callback<JsonObject> response);

        @POST("/Discover/v1/floatingpoint/generateAPK")
        void getGenerate(@Query("clientId") String clientId, @Query("fpId") String fpId, Callback<JsonObject> response);

        @POST("/Discover/v1/floatingpoint/getPlayStoreAPKstatus")
        void getPublishStatus(@Query("clientId") String clientId, @Query("fpId") String fpId, Callback<List<StoreAndGoModel.PublishStatusModel>> response);

        @POST("/Discover/v1/floatingpoint/getAPKScreenshots")
        void getScreenshots(@Query("clientId") String clientId, @Query("fpId") String fpId, Callback<List<StoreAndGoModel.ScreenShotsModel>> response);
    }
}
