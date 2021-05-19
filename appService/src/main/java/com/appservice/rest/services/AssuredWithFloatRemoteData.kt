package com.appservice.rest.services

import com.appservice.model.pickUpAddress.PickUpAddressResponse
import com.appservice.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AssuredWithFloatRemoteData {

  @GET(EndPoints.PICK_UP_ADDRESS)
  fun getPickUpAddress(@Query("websiteId") fpId: String?): Observable<Response<PickUpAddressResponse>>

  @GET(EndPoints.GET_PRODUCT_INFORMATION_FETCH)
  fun getProductInfoFetch( @Query("sellerId") fpTag: String?): Observable<Response<PickUpAddressResponse>>
}