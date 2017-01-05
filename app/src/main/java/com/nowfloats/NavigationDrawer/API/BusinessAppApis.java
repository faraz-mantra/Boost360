package com.nowfloats.NavigationDrawer.API;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Admin on 1/4/2017.
 */

public class BusinessAppApis {

    private final static String BUSINESS_APIS_END_POINT ="http://54.254.145.195";

     public static AppApis getRestAdapter(){
        RestAdapter adapter=new RestAdapter.Builder()
                .setEndpoint(BUSINESS_APIS_END_POINT)
                .setLog(new AndroidLog("ggg"))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return adapter.create(AppApis.class);
    }
    public interface AppApis{


        @POST("/Discover/v1/floatingpoint/getAPKstatus")
        void getStatus(@Query("clientId") String clientId, @Query("fpId") String fpId, Callback<String> response);

        @POST("/Discover/v1/floatingpoint/downloadAPK")
        void getDownload(@Query("clientId") String clientId,@Query("fpId") String fpId, Callback<String> response);


        @POST("/Discover/v1/floatingpoint/generateAPK")
        void getGenerate(@Query("clientId") String clientId, @Query("fpId") String fpId, Callback<String> response);

        @FormUrlEncoded
        @POST("/Discover/v1/floatingpoint/getAPKScreenshots")
        void getScreenshots(@Query("clientId") String clientId,@Query("fpId") String fpId, Callback<String> response);}
}
