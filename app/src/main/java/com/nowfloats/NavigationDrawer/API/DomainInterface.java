package com.nowfloats.NavigationDrawer.API;

import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by NowFloats on 04-11-2016.
 */

public interface DomainInterface {
    @Headers({"Content-Type: application/json"})
    @GET("/DomainService/v1/GetDomainDetailsForFloatingPoint/{fpTag}")
    public void getDomainDetailsForFloatingPoint(@Path("fpTag") String fpTag, @QueryMap Map<String, String> map, Callback<DomainDetails> callback);

    @Headers({"Content-Type: application/json"})
    @GET("/DomainService/v1/domains/supportedTypes")
    public void getDomainSupportedTypes(@QueryMap Map<String, String> map, Callback<List<String>> callback);

    @Headers({"Content-Type: application/json"})
    @GET("/DomainService/v1/checkAvailability/{domainName}")
    public void checkDomainAvailability(@Path("domainName") String domainName,@QueryMap Map<String, String> map, Callback<Boolean> callback);

    @Headers({"Content-Type: application/json"})
    @POST("/api/Service/EmailRIASupportTeamV2")
    public void linkDomain(@QueryMap Map<String, String> map,@Body Map<String,String> bodyMap, Callback<Boolean> callback);

    @Headers({"Content-Type: application/json"})
    @PUT("/DomainService/v1/domainWithWebsite/create")
    public void buyDomain(@Body Map<String,String> bodyMap, Callback<String> callback);

    @GET("/Discover/v3/floatingPoint/nf-app/{fpid}")
    public void getFPDetails(@Path("fpid") String fpid,@QueryMap Map<String,String> map, Callback<Get_FP_Details_Model> callback);

}