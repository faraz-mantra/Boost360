package com.boost.upgrades.data.remote

import com.boost.upgrades.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.upgrades.data.api_model.GetFloatingPointWebWidgets.response.GetFloatingPointWebWidgetsResponse
import com.boost.upgrades.data.api_model.GetPurchaseOrder.GetPurchaseOrderResponse
import com.boost.upgrades.data.api_model.PaymentThroughEmail.PaymentThroughEmailRequestBody
import com.boost.upgrades.data.api_model.PurchaseOrder.requestV2.CreatePurchaseOrderV2
import com.boost.upgrades.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.upgrades.data.api_model.RazorpayToken.RazorpayTokenResponse
import com.boost.upgrades.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.upgrades.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.upgrades.data.api_model.customerId.get.GetCustomerIDResponse
import com.boost.upgrades.data.renewalcart.CreateCartResponse
import com.boost.upgrades.data.renewalcart.CreateCartStateRequest
import com.boost.upgrades.data.renewalcart.RenewalPurchasedResponse
import io.reactivex.Observable
import retrofit2.http.*

interface ApiInterface {

  @Headers("Authorization: 591c0972ee786cbf48bd86cf", "Content-Type: application/json")
  @GET("https://developer.api.boostkit.dev/language/v1/upgrade/get-data?website=5e7a3cf46e0572000109a5b2")
  fun GetAllFeatures(): Observable<GetAllFeaturesResponse>

  @Headers("Content-Type: application/json")
  @GET("Payment/v9/floatingpoint/CustomerPaymentProfile/{internalSourceId}")
  fun getCustomerId(@Path("internalSourceId") internalSourceId: String, @Query("clientId") clientId: String): Observable<GetCustomerIDResponse>

  @Headers("Content-Type: application/json")
  @POST("Payment/v9/floatingpoint/CreateCustomerPaymentProfile")
  fun createCustomerId(@Body customerData: CreateCustomerInfoRequest): Observable<CreateCustomerIDResponse>

  @Headers("Content-Type: application/json")
  @PUT("Payment/v9/floatingpoint/UpdateCustomerPaymentProfile")
  fun updateCustomerId(@Body customerData: CreateCustomerInfoRequest): Observable<CreateCustomerIDResponse>

//    @Headers("Content-Type: application/json")
//    @POST("Payment/v9/floatingpoint/CreatePurchaseOrder")
//    fun CreatePurchaseOrder(@Body createPurchaseOrderRequest: CreatePurchaseOrderRequest): Observable<CreatePurchaseOrderResponse>

  @Headers("Content-Type: application/json")
  @POST("http://api2.withfloats.com/Payment/v10/floatingpoint/CreatePurchaseOrder")
  fun CreatePurchaseOrder(@Body createPurchaseOrderV2: CreatePurchaseOrderV2): Observable<CreatePurchaseOrderResponse>

  @Headers("Content-Type: application/json")
  @GET("discover/v9/floatingPoint/FloatingPointWebWidgets/{floatingPointId}")
  fun GetFloatingPointWebWidgets(@Path("floatingPointId") floatingPointId: String, @Query("clientId") clientId: String): Observable<GetFloatingPointWebWidgetsResponse>

  @Headers("Content-Type: application/json")
  @GET("https://api.razorpay.com/v1/customers/{customer_id}/tokens")
  fun getRazorPayTokens(@Header("Authorization") authHeader: String, @Path("customer_id") customerId: String): Observable<RazorpayTokenResponse>

  @Headers("Content-Type: application/json")
  @GET("https://api.withfloats.com/Payment/v10/floatingpoint/PurchaseOrders/{floatingPointId}")
  fun getPurchasedOrders(@Path("floatingPointId") floatingPointId: String, @Query("clientId") clientId: String): Observable<GetPurchaseOrderResponse>

    @Headers("Content-Type: application/json")
    @POST("https://api2.withfloats.com/Internal/v1/PushEmailToQueue/{clientId}")
    fun createPaymentThroughEmail(@Path("clientId") clientId: String, @Body data: PaymentThroughEmailRequestBody): Observable<String?>

  @Headers("Content-Type: application/json")
  @GET("Payment/v9/floatingpoint/AllPurchasedWidgets/{floatingPointId}")
  fun allPurchasedWidgets(@Path("floatingPointId") floatingPointId: String?,
                          @Query("clientId") clientId: String?,
                          @Query("widgetStatus") widgetStatus: String?,
                          @Query("widgetKey") widgetKey: String?,
                          @Query("nextWidgetStatus") nextWidgetStatus: String?,
                          @Query("dateFilter") dateFilter: String?,
                          @Query("startDate") startDate: String?,
                          @Query("endDate") endDate: String?): Observable<RenewalPurchasedResponse>

  @Headers("Content-Type: application/json")
  @POST("Payment/v9/floatingpoint/widgets/CartState")
  fun createCartStateRenewal(@Body request: CreateCartStateRequest): Observable<CreateCartResponse>
}