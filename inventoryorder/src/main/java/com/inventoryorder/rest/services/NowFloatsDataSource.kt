package com.inventoryorder.rest.services

import com.inventoryorder.model.spaAppointment.GetServiceListingResponse
import com.inventoryorder.model.spaAppointment.bookingslot.request.BookingSlotsRequest
import com.inventoryorder.model.spaAppointment.bookingslot.response.BookingSlotResponse
import com.inventoryorder.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NowFloatsDataSource {

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
}