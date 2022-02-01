package com.boost.dbcenterapi.data.rest.services

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.dbcenterapi.data.api_model.GetFloatingPointWebWidgets.response.GetFloatingPointWebWidgetsResponse
import com.boost.dbcenterapi.data.api_model.couponRequest.CouponRequest
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponResponse
import com.boost.dbcenterapi.data.api_model.getCouponResponse.GetCouponResponse
import com.boost.dbcenterapi.data.rest.EndPoints
import com.framework.base.BaseResponse
import com.framework.models.UserProfileData
import com.framework.pref.clientId2
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MarketplaceNewApiInterface {

  @Headers("Authorization: 591c0972ee786cbf48bd86cf", "Content-Type: application/json")
  @GET("https://developer.api.boostkit.dev/language/v1/upgrade/get-data?website=5e7a3cf46e0572000109a5b2")
  fun GetAllFeatures(): Observable<Response<GetAllFeaturesResponse>>

  @Headers(
    "Authorization: Basic YXBpbW9kaWZpZXI6dkVFQXRudF9yJ0RWZzcofg==",
    "Content-Type: application/json"
  )
  @POST("https://si-withfloats-coupons-api-appservice.azurewebsites.net/v1/coupons/redeem")
  fun redeemCoupon(@Body redeemCouponRequest: RedeemCouponRequest): Observable<Response<RedeemCouponResponse>>


}