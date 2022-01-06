package com.boost.dbcenterapi.data.rest.services

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.dbcenterapi.data.api_model.GetFloatingPointWebWidgets.response.GetFloatingPointWebWidgetsResponse
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.GetPurchaseOrderResponse
import com.boost.dbcenterapi.data.api_model.PaymentThroughEmail.PaymentPriorityEmailRequestBody
import com.boost.dbcenterapi.data.api_model.PaymentThroughEmail.PaymentThroughEmailRequestBody
import com.boost.dbcenterapi.data.api_model.PurchaseOrder.requestV2.CreatePurchaseOrderV2
import com.boost.dbcenterapi.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.dbcenterapi.data.api_model.RazorpayToken.RazorpayTokenResponse
import com.boost.dbcenterapi.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.dbcenterapi.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.dbcenterapi.data.api_model.customerId.get.GetCustomerIDResponse
import com.boost.dbcenterapi.data.api_model.gst.GSTApiResponse
import com.boost.dbcenterapi.data.api_model.paymentprofile.GetLastPaymentDetails
import com.boost.dbcenterapi.data.api_model.stateCode.GetStates
import com.boost.dbcenterapi.data.renewalcart.CreateCartResponse
import com.boost.dbcenterapi.data.renewalcart.CreateCartStateRequest
import com.boost.dbcenterapi.data.renewalcart.RenewalPurchasedResponse
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

interface MarketplaceApiInterface {

//  @Headers("Authorization: 591c0972ee786cbf48bd86cf", "Content-Type: application/json")
//  @GET("https://developer.api.boostkit.dev/language/v1/upgrade/get-data?website=5e7a3cf46e0572000109a5b2")
//  fun GetAllFeatures(): Observable<Response<GetAllFeaturesResponse>>

  @Headers("Content-Type: application/json")
  @GET("Payment/v9/floatingpoint/CustomerPaymentProfile/{internalSourceId}")
  fun getCustomerId(
    @Header("Authorization") auth: String,
    @Path("internalSourceId") internalSourceId: String,
    @Query("clientId") clientId: String
  ): Observable<Response<GetCustomerIDResponse>>

  @Headers("Content-Type: application/json")
  @POST("Payment/v9/floatingpoint/CreateCustomerPaymentProfile")
  fun createCustomerId(@Header("Authorization") auth: String, @Body customerData: CreateCustomerInfoRequest): Observable<Response<CreateCustomerIDResponse>>

  @Headers("Content-Type: application/json")
  @PUT("Payment/v9/floatingpoint/UpdateCustomerPaymentProfile")
  fun updateCustomerId(@Header("Authorization") auth: String, @Body customerData: CreateCustomerInfoRequest): Observable<Response<CreateCustomerIDResponse>>

//    @Headers("Content-Type: application/json")
//    @POST("Payment/v9/floatingpoint/CreatePurchaseOrder")
//    fun CreatePurchaseOrder(@Body createPurchaseOrderRequest: CreatePurchaseOrderRequest): Observable<Response<CreatePurchaseOrderResponse>>

  @Headers("Content-Type: application/json")
  @POST("http://api2.withfloats.com/Payment/v10/floatingpoint/CreatePurchaseOrder")
  fun CreatePurchaseOrder(@Header("Authorization") auth: String, @Body createPurchaseOrderV2: CreatePurchaseOrderV2): Observable<Response<CreatePurchaseOrderResponse>>

  @Headers("Content-Type: application/json")
  @POST("http://api2.withfloats.com/Payment/v11/floatingpoint/CreatePurchaseOrder")
  fun CreatePurchaseAutoRenewOrder(@Header("Authorization") auth: String, @Body createPurchaseOrderV2: CreatePurchaseOrderV2): Observable<Response<CreatePurchaseOrderResponse>>

  @Headers("Content-Type: application/json")
  @GET("discover/v9/floatingPoint/FloatingPointWebWidgets/{floatingPointId}")
  fun GetFloatingPointWebWidgets(
    @Path("floatingPointId") floatingPointId: String,
    @Query("clientId") clientId: String
  ): Observable<Response<GetFloatingPointWebWidgetsResponse>>

  @Headers("Content-Type: application/json")
  @GET("https://api.razorpay.com/v1/customers/{customer_id}/tokens")
  fun getRazorPayTokens(
    @Header("Authorization") authHeader: String,
    @Path("customer_id") customerId: String
  ): Observable<Response<RazorpayTokenResponse>>

  @Headers("Content-Type: application/json")
  @GET("https://api.withfloats.com/Payment/v10/floatingpoint/PurchaseOrders/{floatingPointId}")
  fun getPurchasedOrders(
    @Header("Authorization") auth: String,
    @Path("floatingPointId") floatingPointId: String,
    @Query("clientId") clientId: String
  ): Observable<Response<GetPurchaseOrderResponse>>

  @Headers("Content-Type: application/json")
  @POST("https://api2.withfloats.com/Internal/v1/PushEmailToQueue/{clientId}")
  fun createPaymentThroughEmail(
    @Header("Authorization") auth: String,
    @Path("clientId") clientId: String,
    @Body data: PaymentThroughEmailRequestBody
  ): Observable<Response<String?>>

  @Headers("Content-Type: application/json")
  @POST("https://api.withfloats.com/discover/v1/FloatingPoint/SendEmailWithPriority/")
  fun createPaymentThroughEmailPriority(@Header("Authorization") auth: String, @Body data: PaymentPriorityEmailRequestBody): Observable<Response<String?>>

  @Headers("Content-Type: application/json")
  @GET("Payment/v9/floatingpoint/AllPurchasedWidgets/{floatingPointId}")
  fun allPurchasedWidgets(
    @Header("Authorization") auth: String,
    @Path("floatingPointId") floatingPointId: String?,
    @Query("clientId") clientId: String?,
    @Query("widgetStatus") widgetStatus: String?,
    @Query("widgetKey") widgetKey: String?,
    @Query("nextWidgetStatus") nextWidgetStatus: String?,
    @Query("dateFilter") dateFilter: String?,
    @Query("startDate") startDate: String?,
    @Query("endDate") endDate: String?
  ): Observable<Response<RenewalPurchasedResponse>>

  @Headers("Content-Type: application/json")
  @POST("Payment/v9/floatingpoint/widgets/CartState")
  fun createCartStateRenewal(@Header("Authorization") auth: String, @Body request: CreateCartStateRequest): Observable<Response<CreateCartResponse>>

//  @Headers(
//    "Authorization: Basic YXBpbW9kaWZpZXI6dkVFQXRudF9yJ0RWZzcofg==",
//    "Content-Type: application/json"
//  )
//  @POST("https://si-withfloats-coupons-api-appservice.azurewebsites.net/v1/coupons/redeem")
//  fun redeemCoupon(@Body redeemCouponRequest: RedeemCouponRequest): Observable<Response<RedeemCouponResponse>>

  @Headers("Content-Type: application/json")
  @GET("https://api2.withfloats.com/api/v1/Business/GetGSTINInformation")
  fun getGSTDetails(
    @Header("Authorization") auth: String,
    @Query("gstin") gstIN: String?,
    @Query("clientId") clientId: String?
  ): Observable<Response<GSTApiResponse>>

  @Headers("Content-Type: application/json")
  @GET("https://api2.withfloats.com/api/v1/Business/GetStateCode")
  fun getStates(
    @Header("Authorization") auth: String,
    @Query("clientId") clientId: String?
  ): Observable<Response<GetStates>>

  @Headers("Content-Type: application/json")
  @GET("https://api.withfloats.com/discover/v9/business/paymentProfile/{floatingPointId}")
  fun getLastPaymentDetails(
    @Header("Authorization") auth: String,
    @Path("floatingPointId") floatingPointId: String?,
    @Query("clientId") clientId: String?
  ):Observable<Response<GetLastPaymentDetails>>
  
}