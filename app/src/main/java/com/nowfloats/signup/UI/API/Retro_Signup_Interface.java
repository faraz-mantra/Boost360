package com.nowfloats.signup.UI.API;


import com.nowfloats.Store.Model.PricingPlansModel;
import com.nowfloats.signup.UI.Model.Email_Validation_Model;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;
import com.nowfloats.signup.UI.Model.Get_Feature_Details;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by NowFloatsDev on 13/05/2015.
 */
public interface Retro_Signup_Interface {


//    String url = "https://bpi.briteverify.com/emails.json?" +
//            "address="+email+
//            "&" +
//            "apikey=e5f5fb5a-8e1f-422e-9d25-a67a16018d47"
//            ;
    @GET("/emails.json")
    void get_IsValidEmail(@QueryMap Map<String,String> map, Callback<Email_Validation_Model> callback);
    //public static String uniqueNumber = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/verifyPrimaryNumber";

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/Discover/v1/floatingPoint/verifyPrimaryNumber")
    void post_verifyUniqueNumber(@Body HashMap<String,String> map, Callback<Boolean> callback);


    //https://api.withfloats.com/Discover/v3/FloatingPoint/create

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @PUT("/Discover/v5/FloatingPoint/create")
    void put_createStore(@Body HashMap<String,String> map, @Query("existingProfileId") String existingProfileId, Callback<String> callback);


   // Constants.NOW_FLOATS_API_URL+"/Discover/v1/floatingPoint/suggestTag";

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/Discover/v1/floatingPoint/suggestTag")
    void post_SuggestTag(@Body HashMap<String,String> map, Callback<String> callback);

    //Constants.NOW_FLOATS_API_URL+"/Discover/v1/floatingPoint/verifyUniqueTag";

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/Discover/v1/floatingPoint/verifyUniqueTag")
    void post_verifyTag(@Body HashMap<String,String> map, Callback<String> callback);

    //    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("/Discover/v3/floatingPoint/nf-app/{fpid}")
    void post_getFPDetails(@Path("fpid") String fpid,@QueryMap Map<String,String> map, Callback<Get_FP_Details_Model> callback);

    @GET("/Features/v1/GetFeatureDetils")
    void post_getFeatureDetails(@Query("fpId") String fdId,@Query("clientId") String clientId, Callback<Get_Feature_Details> callback);

    @GET("/Support/v5/floatingpoint/getpackages")
    void post_getFPPackageDetails(@QueryMap Map<String,String> map, Callback<PricingPlansModel> callback);
}