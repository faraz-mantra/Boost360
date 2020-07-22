package com.nowfloats.hotel.API;

import com.nowfloats.AccrossVerticals.API.model.testimonials.TestimonialModel;
import com.nowfloats.hotel.API.model.AddPlacesAround.AddPlacesAroundRequest;
import com.nowfloats.hotel.API.model.DeletePlacesAround.DeletePlacesAroundRequest;
import com.nowfloats.hotel.API.model.GetPlacesAround.GetPlacesAroundModel;
import com.nowfloats.hotel.API.model.UpdatePlacesAround.UpdatePlacesAroundRequest;

import org.json.JSONObject;

import okhttp3.Response;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

public interface HotelAPIInterfaces {

    @Headers({"Authorization: 59c8add5dd304111404e7f04"})
    @GET("/api/v1/placesaround/get-data")
    void getPlacesAroundList(@Query("query") JSONObject query, @Query("skip") int skip, @Query("limit") int limit, Callback<GetPlacesAroundModel> response);

    @Headers({"Authorization: 59c8add5dd304111404e7f04","Content-Type: application/json"})
    @POST("/api/v1/placesaround/add-data")
    void addPlacesAround(@Body AddPlacesAroundRequest body, Callback<String> response);

    @Headers({"Authorization: 59c8add5dd304111404e7f04","Content-Type: application/json"})
    @POST("/api/v1/placesaround/update-data")
    void deletePlacesAround(@Body DeletePlacesAroundRequest body, Callback<String> response);

    @Headers({"Authorization: 59c8add5dd304111404e7f04","Content-Type: application/json"})
    @POST("/api/v1/placesaround/update-data")
    void updatePlacesAround(@Body UpdatePlacesAroundRequest body, Callback<String> response);



}