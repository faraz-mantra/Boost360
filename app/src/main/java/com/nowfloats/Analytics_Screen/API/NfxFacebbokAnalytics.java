package com.nowfloats.Analytics_Screen.API;

import com.nowfloats.Analytics_Screen.model.GetFacebookAnalyticsData;
import com.nowfloats.Analytics_Screen.model.NfxGetTokensResponse;
import com.nowfloats.util.Constants;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * Created by Abhi on 12/1/2016.
 */

public class NfxFacebbokAnalytics {
    public static nfxFacebookApis getAdapter(){
        RestAdapter adapter = new RestAdapter.Builder()
               /* .setLog(new AndroidLog("ggg"))
                .setLogLevel(RestAdapter.LogLevel.FULL)*/
                .setEndpoint(Constants.NFX_WITH_NOWFLOATS)
                .build();
        return adapter.create(nfxFacebookApis.class);
    }

    public interface nfxFacebookApis {

        @Headers({
                "key:78234i249123102398",
                "pwd:JYUYTJH*(*&BKJ787686876bbbhl)",
                "Content-Type:application/json"})
        @GET("/dataexchange/v1/fetch/analytics")
        void nfxFetchFacebookData(@Query("nowfloats_id") String id, @Query("identifier") String facebook, Callback<GetFacebookAnalyticsData> response);

        @Headers({
                "key:78234i249123102398",
                "pwd:JYUYTJH*(*&BKJ787686876bbbhl)",
                "Content-Type:application/json"})
        @GET("/dataexchange/v1/getAccessTokens")
        void nfxGetSocialTokens(@Query("nowfloats_id") String id, Callback<NfxGetTokensResponse> callback);

    }
}
