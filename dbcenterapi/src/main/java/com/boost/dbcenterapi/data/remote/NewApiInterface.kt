package com.boost.dbcenterapi.data.remote

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.dbcenterapi.data.api_model.couponRequest.CouponRequest
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponResponse
import com.boost.dbcenterapi.data.api_model.helpModule.HelpModule
import com.boost.dbcenterapi.data.api_model.getCouponResponse.GetCouponResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface NewApiInterface {

  @Headers("Authorization: 591c0972ee786cbf48bd86cf", "Content-Type: application/json")
  @GET("https://developer.api.boostkit.dev/language/v1/upgrade/get-data?website=5e7a3cf46e0572000109a5b2")
  fun GetAllFeatures(): Observable<GetAllFeaturesResponse>

  @Headers("Authorization: 597ee93f5d64370820a6127c", "Content-Type: application/json")
  @GET("https://developer.api.boostkit.dev/language/v1/featurevideos/get-data?website=61278bf6f2e78f0001811865")
  fun GetHelp(): Observable<HelpModule>

  @Headers(
    "Authorization: Basic YXBpbW9kaWZpZXI6dkVFQXRudF9yJ0RWZzcofg==",
    "Content-Type: application/json"
  )
  @POST("https://si-withfloats-coupons-api-appservice.azurewebsites.net/v1/coupons/redeem")
  fun redeemCoupon(@Body redeemCouponRequest: RedeemCouponRequest): Observable<RedeemCouponResponse>

  @Headers("Authorization: 591c0972ee786cbf48bd86cf")
  @POST("https://developer.api.boostkit.dev/language/v1/5e5877a701921c02011ca983/get-bulk-data-by-property/DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70")
  fun getOfferCoupons(@Body couponRequest: CouponRequest):Observable<GetCouponResponse>
}