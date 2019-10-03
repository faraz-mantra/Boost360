package com.nowfloats.BusinessProfile.UI.Model;

import android.util.Log;

//import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by NowFloatsDev on 28/05/2015.
 */
public class ServiceGenerator {

    // No need to instantiate this class.
    private ServiceGenerator() {
    }

    /*public static <S> S createService(Class<S> serviceClass, String baseUrl) {



        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setClient(new OkClient(new OkHttpClient()))
                 .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String msg) {
                        String[] blacklist = {"Access-Control", "Cache-Control", "Connection", "Content-Type", "Keep-Alive", "Pragma", "Server", "Vary", "X-Powered-By"};
                        for (String bString : blacklist) {
                            if (msg.startsWith(bString)) {
                                return;
                            }
                        }
                        Log.d("Retrofit", msg);
                    }
                });

        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);
    }*/
}
