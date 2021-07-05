package com.appservice.rest.services

import com.appservice.model.account.testimonial.addEdit.DeleteTestimonialRequest
import com.appservice.model.serviceTiming.AddServiceTimingRequest
import com.appservice.model.serviceTiming.ServiceTimingResponse
import com.appservice.model.staffModel.*
import com.appservice.rest.EndPoints
import com.appservice.ui.testimonial.newflow.model.*
import com.appservice.ui.testimonial.newflow.model.DataItem
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StaffNowFloatsRemoteData {
  @POST(EndPoints.CREATE_STAFF_PROFILE)
  fun createStaffProfile(@Body request: StaffCreateProfileRequest?): Observable<Response<StaffCreateProfileResponse>>

  @POST(EndPoints.FETCH_STAFF_SERVICES)
  fun fetchServices(@Body request: ServiceListRequest?): Observable<Response<ServiceListResponse>>

  @POST(EndPoints.GET_STAFF_LISTING)
  fun fetchStaffList(@Body request: GetStaffListingRequest?): Observable<Response<GetStaffListingResponse>>

  @POST(EndPoints.STAFF_PROFILE_UPDATE)
  fun staffProfileUpdate(@Body request: StaffProfileUpdateRequest?): Observable<Response<ResponseUpdateProfile>>

  @POST(EndPoints.STAFF_PROFILE_DELETE)
  fun staffProfileDelete(@Body request: StaffDeleteImageProfileRequest?): Observable<Response<BaseResponse>>

  @POST(EndPoints.STAFF_DELETE_IMAGE)
  fun staffImageDelete(@Body request: StaffDeleteImageProfileRequest?): Observable<Response<BaseResponse>>

  @POST(EndPoints.STAFF_ADD_TIMING)
  fun staffAddTimings(@Body request: StaffTimingAddUpdateRequest?): Observable<Response<BaseResponse>>

  @POST(EndPoints.STAFF_UPDATE_TIMING)
  fun staffUpdateTimings(@Body request: StaffTimingAddUpdateRequest?): Observable<Response<BaseResponse>>

  @POST(EndPoints.STAFF_UPDATE_IMAGE)
  fun staffUpdateImage(@Body request: StaffUpdateImageRequest?): Observable<Response<BaseResponse>>

  @GET(EndPoints.GET_STAFF_DETAILS)
  fun staffDetails(@Query(value = "staffId") staffId: String?): Observable<Response<StaffDetailsResponse>>

  // Service timing
  @POST(EndPoints.POST_ADD_SERVICE_TIMING)
  fun addServiceTiming(@Body requestWeeklyAppointment: AddServiceTimingRequest?): Observable<Response<ResponseBody>>

  @POST(EndPoints.POST_UPDATE_SERVICE_TIMING)
  fun updateServiceTiming(@Body requestWeeklyAppointment: AddServiceTimingRequest?): Observable<Response<ResponseBody>>

  @GET(EndPoints.GET_SERVICE_TIMING)
  fun getServiceTimings(@Query("serviceId") serviceId: String?): Observable<Response<ServiceTimingResponse>>

  @POST(EndPoints.GET_TESTIMONIAL_LISTING)
  fun fetchTestimonialList(@Body request: TestimonialListingRequest?): Observable<Response<TestimonialListingResponse>>

  @GET(EndPoints.GET_TESTIMONIAL_DETAILS)
  fun getTestimonialData(@Query(value = "TestimonialId") testimonialId: String?): Observable<Response<TestimonialResponse>>

  @POST(EndPoints.CREATE_TESTIMONIAL_POST)
  fun createTestimonial(@Body request: DataItem?): Observable<Response<BaseResponse>>

  @POST(EndPoints.UPDATE_TESTIMONIAL)
  fun updateTestimonial(@Body request: DataItem?): Observable<Response<BaseResponse>>

  @POST(EndPoints.UPDATE_TESTIMONIAL_IMAGE_POST)
  fun updateTestimonialImage(@Body request: UpdateTestimonialImage?): Observable<Response<BaseResponse>>

  @POST(EndPoints.DELETE_TESTIMONIAL_POST)
  fun deleteTestimonial(@Body request: DeleteTestimonialRequestNew?): Observable<Response<BaseResponse>>

  @POST(EndPoints.DELETE_TESTIMONIAL_IMAGE_POST)
  fun deleteTestimonialImage(@Body request: DeleteTestimonialRequestNew?): Observable<Response<BaseResponse>>
}