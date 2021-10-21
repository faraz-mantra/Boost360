package com.inventoryorder.rest.services

import GetStaffListingRequest
import com.inventoryorder.model.doctorsData.GetStaffListingResponse
import com.inventoryorder.model.services.ServiceListingRequest
import com.inventoryorder.model.services.ServiceListingResponse
import com.inventoryorder.model.services.general.GeneralServiceResponse
import com.inventoryorder.model.spaAppointment.GetServiceListingResponse
import com.inventoryorder.model.spaAppointment.bookingslot.request.BookingSlotsRequest
import com.inventoryorder.model.spaAppointment.bookingslot.response.BookingSlotResponse
import com.inventoryorder.model.spaAppointment.bookingslot.response.ResultSlot
import com.inventoryorder.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface NowFloatsDataSource {

  @POST(EndPoints.GET_SERVICE_LISTING)
  fun getServiceListing(@Body request: ServiceListingRequest, ): Observable<Response<ServiceListingResponse>>

  @GET(EndPoints.GET_SEARCH_LISTING)
  fun getSearchListing(
    @Query("fpTag") fpTag: String?,
    @Query("fpId") fpId: String?,
    @Query("searchString") searchString: String?,
    @Query("offset") offset: Int?,
    @Query("limit") limit: Int?,
  ): Observable<Response<GetServiceListingResponse>>

  @POST(EndPoints.GET_BOOKING_SLOTS)
  fun getBookingSlots(@Body request: BookingSlotsRequest?): Observable<Response<BookingSlotResponse>>

  @POST(EndPoints.GET_BOOKING_SLOTS_STAFF)
  fun getBookingSlotsStaff(@Path("staffId") staffId:String?,@Body request: BookingSlotsRequest?): Observable<Response<Array<ResultSlot>>>

  @POST(EndPoints.GET_STAFF_LISTING)
  fun fetchStaffList(@Body request: GetStaffListingRequest?): Observable<Response<GetStaffListingResponse>>

  @GET(EndPoints.GET_GENERAL_SERVICE)
  fun getGeneralService(
    @Query("fpTag") fpTag: String?,
    @Query("fpId") fpId: String?
  ): Observable<Response<GeneralServiceResponse>>
}