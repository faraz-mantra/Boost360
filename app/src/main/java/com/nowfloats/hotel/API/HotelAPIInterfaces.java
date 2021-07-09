package com.nowfloats.hotel.API;

import com.nowfloats.hotel.API.model.AddOffer.AddOfferRequest;
import com.nowfloats.hotel.API.model.AddPlacesAround.AddPlacesAroundRequest;
import com.nowfloats.hotel.API.model.AddTripAdvisorData.AddTripAdvisorDataRequest;
import com.nowfloats.hotel.API.model.DeleteOffer.DeleteOfferRequest;
import com.nowfloats.hotel.API.model.DeletePlacesAround.DeletePlacesAroundRequest;
import com.nowfloats.hotel.API.model.GetOffers.GetOffersResponse;
import com.nowfloats.hotel.API.model.GetPlacesAround.GetPlacesAroundModel;
import com.nowfloats.hotel.API.model.GetTripAdvisorData.GetTripAdvisorData;
import com.nowfloats.hotel.API.model.UpdateOffer.UpdateOfferRequest;
import com.nowfloats.hotel.API.model.UpdatePlacesAround.UpdatePlacesAroundRequest;
import com.nowfloats.hotel.API.model.UpdateTripAdvisorData.UpdateTripAdvisorDataRequest;

import org.json.JSONObject;

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


    @Headers({"Authorization: 59c8add5dd304111404e7f04"})
    @GET("/api/v1/trip_advisor/get-data")
    void getTripAdvisorData(@Query("query") JSONObject query, @Query("skip") int skip, @Query("limit") int limit, Callback<GetTripAdvisorData> response);

    @Headers({"Authorization: 59c8add5dd304111404e7f04","Content-Type: application/json"})
    @POST("/api/v1/trip_advisor/add-data")
    void addTripAdvisorData(@Body AddTripAdvisorDataRequest body, Callback<String> response);

    @Headers({"Authorization: 59c8add5dd304111404e7f04","Content-Type: application/json"})
    @POST("/api/v1/trip_advisor/update-data")
    void updateTripAdvisorData(@Body UpdateTripAdvisorDataRequest body, Callback<String> response);

    @Headers({"Authorization: 59c8add5dd304111404e7f04"})
    @GET("/api/v1/offers/get-data")
    void getOffersList(@Query("query") JSONObject query, @Query("skip") int skip, @Query("limit") int limit, Callback<GetOffersResponse> response);


    @Headers({"Authorization: 59c8add5dd304111404e7f04","Content-Type: application/json"})
    @POST("/api/v1/offers/add-data")
    void addOffer(@Body AddOfferRequest body, Callback<String> response);

    @Headers({"Authorization: 59c8add5dd304111404e7f04","Content-Type: application/json"})
    @POST("/api/v1/offers/update-data")
    void updateOffer(@Body UpdateOfferRequest body, Callback<String> response);

    @Headers({"Authorization: 59c8add5dd304111404e7f04","Content-Type: application/json"})
    @POST("/api/v1/offers/update-data")
    void deleteOffer(@Body DeleteOfferRequest body, Callback<String> response);

}