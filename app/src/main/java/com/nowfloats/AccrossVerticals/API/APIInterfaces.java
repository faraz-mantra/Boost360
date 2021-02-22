package com.nowfloats.AccrossVerticals.API;

import com.nowfloats.AccrossVerticals.API.model.AddTestimonials.AddTestimonialsData;
import com.nowfloats.AccrossVerticals.API.model.DeleteTestimonials.DeleteTestimonialsData;
import com.nowfloats.AccrossVerticals.API.model.ExistingDomain.ExistingDomainRequest;
import com.nowfloats.AccrossVerticals.API.model.GetDomain.GetDomainData;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.GetTestimonialData;
import com.nowfloats.AccrossVerticals.API.model.GetToken.GetTokenData;
import com.nowfloats.AccrossVerticals.API.model.UpdateTestimonialsData.UpdateTestimonialsData;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface APIInterfaces {

  @GET("/api/v1/{testimonials}/get-data")
  void getTestimonialsList(@Header("Authorization") String token, @Path("testimonials") String testimonialType, @Query("query") JSONObject query, @Query("skip") int skip, @Query("limit") int limit, Callback<GetTestimonialData> response);

////    @Headers({"Authorization: 59c8add5dd304111404e7f04"})
//    @GET("/api/v1/testimonials/get-data")
//    void getHotelsTestimonialsList(@Header("Authorization") String token,@Query("query") JSONObject query, @Query("skip") int skip, @Query("limit") int limit, Callback<GetTestimonialData> response);

  @POST("/api/v1/{testimonials}/add-data")
  void addTestimonials(@Header("Authorization") String token, @Path("testimonials") String testimonialType, @Body AddTestimonialsData body, Callback<String> response);

  @POST("/api/v1/{testimonials}/update-data")
  void updateTestimonials(@Header("Authorization") String token, @Path("testimonials") String testimonialType, @Body UpdateTestimonialsData body, Callback<String> response);

  //    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1","Content-Type: application/json"})
  @POST("/api/v1/{testimonials}/update-data")
  void deleteTestimonials(@Header("Authorization") String token,@Path("testimonials") String testimonialType, @Body DeleteTestimonialsData body, Callback<String> response);

  @GET("/DomainService/v1/domains/supportedTypes")
  void getDomainSupportType(@Query("clientId") String clientId, Callback<String[]> response);

  @GET("/DomainService/v3/GetDomainDetailsForFloatingPoint/{fpTag}")
  void getDomainDetails(@Path("fpTag") String fpTag, @Query("clientId") String clientId, Callback<GetDomainData> response);

  @POST("/api/Service/EmailRIASupportTeamV2")
  void addExistingDomainDeatils(@Query("authClientId") String clientId, @Query("fpTag") String fpTag, @Body ExistingDomainRequest body, Callback<Boolean> response);

  @GET("/kitsune/v1/ListWebActionDetails/{themeID}")
//    void getHeaderAuthorizationtoken(@Path("themeID") String fpTag, @Query("WebsiteId") String websiteId, Callback<GetDomainData> response);
  void getHeaderAuthorizationtoken(@Path("themeID") String themeID, @Query("WebsiteId") String websiteId, Callback<GetTokenData> response);

}