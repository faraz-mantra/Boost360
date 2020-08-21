package com.nowfloats.AccrossVerticals.API;

import com.nowfloats.AccrossVerticals.API.model.AddTestimonials.AddTestimonialsData;
import com.nowfloats.AccrossVerticals.API.model.DeleteTestimonials.DeleteTestimonialsData;
import com.nowfloats.AccrossVerticals.API.model.ExistingDomain.ExistingDomainRequest;
import com.nowfloats.AccrossVerticals.API.model.ExistingDomain.ExistingDomainResponse;
import com.nowfloats.AccrossVerticals.API.model.GetDomain.GetDomainData;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.GetTestimonialData;
import com.nowfloats.AccrossVerticals.API.model.UpdateTestimonialsData.UpdateTestimonialsData;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface APIInterfaces {

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1"})
    @GET("/api/v1/testimonials/get-data")
    void getTestimonialsList(@Query("query") JSONObject query, @Query("skip") int skip, @Query("limit") int limit, Callback<GetTestimonialData> response);

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1","Content-Type: application/json"})
    @POST("/api/v1/testimonials/add-data")
    void addTestimoinals(@Body AddTestimonialsData body, Callback<String> response);

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1","Content-Type: application/json"})
    @POST("/api/v1/testimonials/update-data")
    void updateTestimoinals(@Body UpdateTestimonialsData body, Callback<String> response);

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1","Content-Type: application/json"})
    @POST("/api/v1/testimonials/update-data")
    void deleteTestimoinals(@Body DeleteTestimonialsData body, Callback<String> response);

    @GET("/DomainService/v1/domains/supportedTypes")
    void getDomainSupportType(@Query("clientId") String clientId, Callback<String[]> response);

    @GET("/DomainService/v3/GetDomainDetailsForFloatingPoint/{fpTag}")
    void getDomainDetails(@Path("fpTag") String fpTag, @Query("clientId") String clientId, Callback<GetDomainData> response);

    @POST("/api/Service/EmailRIASupportTeamV2")
    void addExistingDomainDeatils(@Query("authClientId") String clientId, @Query("fpTag") String fpTag, @Body ExistingDomainRequest body, Callback<Boolean> response);



}