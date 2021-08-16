package com.boost.presignin.rest.services.remote

import com.boost.presignin.model.activatepurchase.ActivatePurchasedOrderRequest
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.model.signup.FloatingPointCreateResponse
import com.boost.presignin.rest.EndPoints
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface BusinessCreateRemoteDataSource {

  @PUT(EndPoints.PUT_CREATE_BUSINESS_URL)
  fun putCreateBusinessV5(
    @Query("existingProfileId") profileId: String?,
    @Body request: BusinessCreateRequest
  ): Observable<Response<String>>

  @PUT(EndPoints.PUT_CREATE_BUSINESS_V6_URL)
  fun putCreateBusinessV6(
    @Query("existingProfileId") profileId: String?,
    @Body request: BusinessCreateRequest
  ): Observable<Response<FloatingPointCreateResponse>>

  @POST(EndPoints.POST_ACTIVATE_PURCHASED_ORDER)
  fun postActivatePurchasedOrder(
    @Query("clientId") clientId: String?,
    @Body request: ActivatePurchasedOrderRequest
  ): Observable<Response<ResponseBody>>
}