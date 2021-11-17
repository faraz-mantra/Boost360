package com.boost.cart.data.remote

import com.boost.cart.data.api_model.GetFloatingPointWebWidgets.response.GetFloatingPointWebWidgetsResponse
import com.boost.cart.data.api_model.GetPurchaseOrder.GetPurchaseOrderResponse
import com.boost.cart.data.api_model.PurchaseOrder.requestV2.CreatePurchaseOrderV2
import com.boost.cart.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.cart.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.cart.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.cart.data.api_model.customerId.get.GetCustomerIDResponse
import com.boost.cart.data.api_model.paymentprofile.GetLastPaymentDetails
import com.boost.cart.data.renewalcart.CreateCartResponse
import com.boost.cart.data.renewalcart.CreateCartStateRequest
import com.boost.cart.data.renewalcart.RenewalPurchasedResponse
import io.reactivex.Observable
import retrofit2.http.*

interface ApiInterface {

  @Headers("Content-Type: application/json")
  @GET("Payment/v9/floatingpoint/CustomerPaymentProfile/{internalSourceId}")
  fun getCustomerId(
    @Header("Authorization") auth: String,
    @Path("internalSourceId") internalSourceId: String,
    @Query("clientId") clientId: String
  ): Observable<GetCustomerIDResponse>

  @Headers("Content-Type: application/json")
  @POST("Payment/v9/floatingpoint/CreateCustomerPaymentProfile")
  fun createCustomerId(@Header("Authorization") auth: String, @Body customerData: CreateCustomerInfoRequest): Observable<CreateCustomerIDResponse>

  @Headers("Content-Type: application/json")
  @PUT("Payment/v9/floatingpoint/UpdateCustomerPaymentProfile")
  fun updateCustomerId(@Header("Authorization") auth: String, @Body customerData: CreateCustomerInfoRequest): Observable<CreateCustomerIDResponse>

//    @Headers("Content-Type: application/json")
//    @POST("Payment/v9/floatingpoint/CreatePurchaseOrder")
//    fun CreatePurchaseOrder(@Body createPurchaseOrderRequest: CreatePurchaseOrderRequest): Observable<CreatePurchaseOrderResponse>

  @Headers("Content-Type: application/json")
  @POST("http://api2.withfloats.com/Payment/v10/floatingpoint/CreatePurchaseOrder")
  fun CreatePurchaseOrder(@Header("Authorization") auth: String, @Body createPurchaseOrderV2: CreatePurchaseOrderV2): Observable<CreatePurchaseOrderResponse>

  @Headers("Content-Type: application/json")
  @POST("http://api2.withfloats.com/Payment/v11/floatingpoint/CreatePurchaseOrder")
  fun CreatePurchaseAutoRenewOrder(@Header("Authorization") auth: String, @Body createPurchaseOrderV2: CreatePurchaseOrderV2): Observable<CreatePurchaseOrderResponse>

  @Headers("Content-Type: application/json")
  @GET("discover/v9/floatingPoint/FloatingPointWebWidgets/{floatingPointId}")
  fun GetFloatingPointWebWidgets(
    @Header("Authorization") auth: String,
    @Path("floatingPointId") floatingPointId: String,
    @Query("clientId") clientId: String
  ): Observable<GetFloatingPointWebWidgetsResponse>

  @Headers("Content-Type: application/json")
  @GET("https://api.withfloats.com/Payment/v10/floatingpoint/PurchaseOrders/{floatingPointId}")
  fun getPurchasedOrders(
    @Header("Authorization") auth: String,
    @Path("floatingPointId") floatingPointId: String,
    @Query("clientId") clientId: String
  ): Observable<GetPurchaseOrderResponse>

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
  ): Observable<RenewalPurchasedResponse>

  @Headers("Content-Type: application/json")
  @POST("Payment/v9/floatingpoint/widgets/CartState")
  fun createCartStateRenewal(@Header("Authorization") auth: String, @Body request: CreateCartStateRequest): Observable<CreateCartResponse>

  @Headers("Content-Type: application/json")
  @GET("https://api.withfloats.com/discover/v9/business/paymentProfile/{floatingPointId}")
  fun getLastPaymentDetails(
    @Header("Authorization") auth: String,
    @Path("floatingPointId") floatingPointId: String?,
    @Query("clientId") clientId: String?
    ):Observable<GetLastPaymentDetails>



}