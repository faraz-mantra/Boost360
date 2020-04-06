package com.biz2.nowfloats.boost.updates.data.remote

import com.boost.upgrades.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.upgrades.data.api_model.GetFloatingPointWebWidgets.response.GetFloatingPointWebWidgetsResponse
import com.boost.upgrades.data.api_model.PurchaseOrder.request.CreatePurchaseOrderRequest
import com.boost.upgrades.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.upgrades.data.api_model.customerId.create.CustomerIDRequest
import com.boost.upgrades.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.upgrades.data.api_model.customerId.get.GetCustomerIDResponse
import com.boost.upgrades.data.model.WidgetModel
import io.reactivex.Observable
import retrofit2.http.*

interface ApiInterface {

    @Headers("Authorization: 591c0972ee786cbf48bd86cf","Content-Type: application/json")
    @GET("https://developer.api.boostkit.dev/language/v1/upgrade/get-data?website=5e7a3cf46e0572000109a5b2")
    fun GetAllFeatures(): Observable<GetAllFeaturesResponse>

    @Headers("Content-Type: application/json")
    @GET("Payment/v9/floatingpoint/CustomerPaymentProfile/{internalSourceId}")
    fun getCustomerId(@Path("internalSourceId") internalSourceId: String ,@Query("clientId") clientId: String): Observable<GetCustomerIDResponse>

    @Headers("Content-Type: application/json")
    @POST("Payment/v9/floatingpoint/CreateCustomerPaymentProfile")
    fun createCustomerId(@Body customerData: CustomerIDRequest): Observable<CreateCustomerIDResponse>

//    @Headers("Content-Type: application/json")
//    @POST("Payment/v9/floatingpoint/CreateCustomerPaymentProfile")
//    fun updateCustomerId(@Body customerData: CustomerIDRequest): Observable<CustomerIdResponse>

    @Headers("Content-Type: application/json")
    @POST("Payment/v9/floatingpoint/CreatePurchaseOrder")
    fun CreatePurchaseOrder(@Body createPurchaseOrderRequest: CreatePurchaseOrderRequest): Observable<CreatePurchaseOrderResponse>

    @Headers("Content-Type: application/json")
    @GET("discover/v9/floatingPoint/FloatingPointWebWidgets/{floatingPointId}")
    fun GetFloatingPointWebWidgets(@Path("floatingPointId") floatingPointId: String,@Query("clientId") clientId: String ): Observable<GetFloatingPointWebWidgetsResponse>
}