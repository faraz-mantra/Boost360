package com.nowfloats.webactions;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.util.Constants;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;

/**
 * Created by NowFloats on 11-04-2018.
 */

public class WebActionNetworkModule {

    private static WebActionNetworkModule sWebActionNetworkModule;

    private IWebActionNetworkInterface mWebActionNetworkInterface;

    private WebActionNetworkModule(String apiBaseUrl) {
        mWebActionNetworkInterface = new RestAdapter.Builder()
                .setEndpoint(apiBaseUrl)
                //.setClient(Constants.getClient())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("ggg"))
                .setConverter(new GsonConverter(new GsonBuilder().setLenient().create()))
                .build()
                .create(IWebActionNetworkInterface.class);
    }

    public static WebActionNetworkModule init(String apiBaseUrl) {
        if (sWebActionNetworkModule == null) {
            sWebActionNetworkModule = new WebActionNetworkModule(apiBaseUrl);
        }

        return sWebActionNetworkModule;
    }

    public IWebActionNetworkInterface getWebActionService() {
        return mWebActionNetworkInterface;
    }
}
