package com.nowfloats.BusinessProfile.UI.API;

import com.nowfloats.NavigationDrawer.API.GetAutoPull;
import com.nowfloats.util.Constants;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by NowFloatsDev on 16/04/2015.
 */

public class Facebook_Auto_Publish_API {
    public static autoPullApi getAdapter() {
        RestAdapter adapter = new RestAdapter.Builder()
                /*.setLog(new AndroidLog("ggg"))
                .setLogLevel(RestAdapter.LogLevel.FULL)*/
                .setEndpoint(Constants.NOW_FLOATS_API_URL)
                .build();
        return adapter.create(autoPullApi.class);
    }

    public interface autoPullApi {
        @GET("/Discover/v1/FloatingPoint/GetFacebookPullRegistrations/{fpId}/{clientId}")
        void getFacebookAutoPull(@Path("fpId") String fpId, @Path("clientId") String clientId,
                                 Callback<GetAutoPull> response);
    }
}
