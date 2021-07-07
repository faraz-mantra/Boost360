package com.appservice.rest.services

import com.appservice.model.account.testimonial.TestimonialDataResponse
import com.appservice.model.account.testimonial.addEdit.DeleteTestimonialRequest
import com.appservice.model.account.testimonial.webActionList.TestimonialWebActionResponse
import com.appservice.model.pickUpAddress.PickUpAddressResponse
import com.appservice.rest.EndPoints
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface BoostKitDevRemoteData {

  @GET(EndPoints.WEB_ACTION_TESTIMONIAL)
  fun getWebActionList(
    @Path("themeID") themeID: String?,
    @Query("WebsiteId") websiteId: String?,
  ): Observable<Response<TestimonialWebActionResponse>>

  @GET(EndPoints.GET_TESTIMONIAL)
  fun getTestimonialsList(
    @Header("Authorization") token: String?,
    @Path("testimonials") testimonialType: String?,
    @Query("query") query: JSONObject?,
    @Query("skip") skip: Int,
    @Query("limit") limit: Int,
  ): Observable<Response<TestimonialDataResponse>>

//  @POST(EndPoints.ADD_TESTIMONIAL)
//  fun addTestimonials(
//      @Header("Authorization") token: String?,
//      @Path("testimonials") testimonialType: String?,
//      @Body body: AddTestimonialsData?,
//  ): Observable<Response<Any>>
//
//  @POST(EndPoints.UPDATE_TESTIMONIAL)
//  fun updateTestimonials(
//      @Header("Authorization") token: String?,
//      @Path("testimonials") testimonialType: String?,
//      @Body body: UpdateTestimonialsData?,
//  ): Observable<Response<Any>>

  @POST(EndPoints.DELETE_TESTIMONIAL)
  fun deleteTestimonials(
    @Header("Authorization") token: String?,
    @Path("testimonials") testimonialType: String?,
    @Body body: DeleteTestimonialRequest?,
  ): Observable<Response<Any>>
}