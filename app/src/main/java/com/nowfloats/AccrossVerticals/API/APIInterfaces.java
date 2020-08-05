package com.nowfloats.AccrossVerticals.API;

import com.nowfloats.AccrossVerticals.API.model.testimonials.TestimonialModel;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

public interface APIInterfaces {

    @Headers({"Authorization: 59ca26afdd30411900b6a8db"})
    @GET("/api/v1/testimonial/get-data")
    void getTestimonialsList(@Query("query") JSONObject query, @Query("skip") int skip, Callback<TestimonialModel> response);

    @GET("/DomainService/v1/domains/supportedTypes")
    void getDomainSupportType(@Query("clientId") String clientId, Callback<String[]> response);



}