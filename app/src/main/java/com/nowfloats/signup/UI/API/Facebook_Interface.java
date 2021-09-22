package com.nowfloats.signup.UI.API;

import com.nowfloats.signup.UI.Model.Facebook_Data_Model;

import org.json.JSONObject;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by NowFloatsDev on 08/06/2015.
 */
public interface Facebook_Interface {


    //  https://graph.facebook.com

    @GET("/me?fields=picture,id,name,gender,email")
    void get_Me_Details(@QueryMap Map<String, String> map, Callback<JSONObject> callback);

    @GET("/{page_id}?fields=picture,id,name,location,description&type=large")
    void get_Me_Accounts_Details(@Path("page_id") String page_id, @QueryMap Map<String, String> map, Callback<Facebook_Data_Model> callback);
}
