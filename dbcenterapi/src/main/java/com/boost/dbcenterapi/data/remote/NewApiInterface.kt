package com.boost.dbcenterapi.data.remote

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponResponse
import com.boost.upgrades.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.upgrades.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.upgrades.data.api_model.couponSystem.redeem.RedeemCouponResponse
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil.kAdminUrl
import io.reactivex.Observable
import retrofit2.http.*

interface NewApiInterface {

  @Headers("Authorization: 591c0972ee786cbf48bd86cf", "Content-Type: application/json")
//  @GET("https://developer.api.boostkit.dev/language/v1/upgrade/get-data?website=5e7a3cf46e0572000109a5b2")
  @GET
  fun GetAllFeatures(@Url url: String? = kAdminUrl()): Observable<GetAllFeaturesResponse>

  @Headers(
    "Authorization: Basic YXBpbW9kaWZpZXI6dkVFQXRudF9yJ0RWZzcofg==",
    "Content-Type: application/json"
  )
  @POST("https://si-withfloats-coupons-api-appservice.azurewebsites.net/v1/coupons/redeem")
  fun redeemCoupon(@Body redeemCouponRequest: RedeemCouponRequest): Observable<RedeemCouponResponse>
}