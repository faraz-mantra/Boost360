package com.onboarding.nowfloats.rest.services.remote.business

import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.model.business.purchasedOrder.ActivatePurchasedOrderRequest
import com.onboarding.nowfloats.model.verification.RequestValidateEmail
import com.onboarding.nowfloats.model.verification.RequestValidatePhone
import com.onboarding.nowfloats.rest.EndPoints
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface BusinessCreateRemoteDataSource {
  @PUT(EndPoints.PUT_CREATE_BUSINESS_URL)
  fun putCreateBusinessOnboarding(@Query("existingProfileId") profileId: String?, @Body request: BusinessCreateRequest): Observable<Response<String>>

  @POST(EndPoints.POST_ACTIVATE_PURCHASED_ORDER)
  fun postActivatePurchasedOrder(@Query("clientId") clientId: String?, @Body request: ActivatePurchasedOrderRequest): Observable<Response<ResponseBody>>

  @POST(EndPoints.VERIFY_EMAIL)
  fun validateUserEmail(@Body requestValidateEmail: RequestValidateEmail?): Observable<Response<ResponseBody>>

  @POST(EndPoints.VERIFY_PHONE)
  fun validateUserPhone(@Body requestValidatePhone: RequestValidatePhone?): Observable<Response<ResponseBody>>
}