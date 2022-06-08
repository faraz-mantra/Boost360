package com.boost.dbcenterapi.data.remote

import com.boost.dbcenterapi.data.api_model.CustomDomain.CustomDomains
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.dbcenterapi.data.api_model.GetFeatureDetails.FeatureDetailsV2Item
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrderV2.GetPurchaseOrderResponseV2
import com.boost.dbcenterapi.data.api_model.call_track.CallTrackListResponse
import com.boost.dbcenterapi.data.api_model.cart.RecommendedAddonsRequest
import com.boost.dbcenterapi.data.api_model.cart.RecommendedAddonsResponse
import com.boost.dbcenterapi.data.api_model.couponRequest.CouponRequest
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponResponse
import com.boost.dbcenterapi.data.api_model.getCouponResponse.GetCouponResponse
import com.boost.dbcenterapi.data.api_model.helpModule.HelpModule
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import io.reactivex.Observable
import retrofit2.http.*

interface NewApiInterface {

    //K-admin API
    @Headers("Content-Type: application/json")
    @GET
    fun GetAllFeatures(
        @Header("Authorization") auth:String="591c0972ee786cbf48bd86cf",
        @Url url: String? = FirebaseRemoteConfigUtil.kAdminUrl()
    ): Observable<GetAllFeaturesResponse>

    //Youtube-videos Marketplace V2
    @Headers("Authorization: 597ee93f5d64370820a6127c", "Content-Type: application/json")
    @GET("https://developer.api.boostkit.dev/language/v1/featurevideos/get-data?website=61278bf6f2e78f0001811865")
    fun GetHelp(): Observable<HelpModule>

    //MyCurrentPlan  Marketplace V2
    @Headers("Content-Type: application/json")
    @GET("https://withfloats-feature-processor-api.azurewebsites.net/Features/v1/GetFeatureDetails")
    fun GetFeatureDetails(
        @Query("fpId") floatingPointId: String,
        @Query("clientId") clientId: String
    ): Observable<ArrayList<FeatureDetailsV2Item>>

    //Coupons CartActivity
    @Headers(
            "Authorization: Basic YXBpbW9kaWZpZXI6dkVFQXRudF9yJ0RWZzcofg==",
            "Content-Type: application/json"
    )
    @POST("https://si-withfloats-coupons-api-appservice.azurewebsites.net/v1/coupons/redeem")
    fun redeemCoupon(@Body redeemCouponRequest: RedeemCouponRequest): Observable<RedeemCouponResponse>

    @Headers("Authorization: 591c0972ee786cbf48bd86cf")
    @POST("https://developer.api.boostkit.dev/language/v1/5e5877a701921c02011ca983/get-bulk-data-by-property/DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70")
    fun getOfferCoupons(@Body couponRequest: CouponRequest): Observable<GetCouponResponse>

    //Recommended Addons Cartscreen
    @Headers("Content-Type: application/json")
    @POST("https://riarecommendationatmarketplacecheckout.azurewebsites.net/getRecommendationAtCheckout")
    fun getRecommendedAddons(@Body recommendedAddonsRequest: RecommendedAddonsRequest): Observable<RecommendedAddonsResponse>

    // MyHistoryOrders V2
    @Headers("Content-Type: application/json")
    @GET("https://api.withfloats.com/Payment/v10/floatingpoint/PurchaseOrders")
    fun getPurchasedOrdersV2(
        @Header("Authorization") auth: String,
        @Query("FloatingPointId") floatingPointId: String
    ): Observable<GetPurchaseOrderResponseV2>

    //Custom Domain FeatureDetails Screen-V3
    @Headers("Content-Type: application/json")
    @POST("https://plugin.withfloats.com/DomainService/v1/DomainSuggestion")
    fun getDomains(@Body domainRequest: DomainRequest):Observable<CustomDomains>

    @Headers("Content-Type: application/json")
    @GET("https://api2.withfloats.com/discover/v2/GetVMN")
    fun getCallTrackDetails(
        @Query("fpId") floatingPointId: String,
        @Query("clientId") clientId: String
    ): Observable<CallTrackListResponse>
}