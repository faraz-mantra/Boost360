package com.biz2.nowfloats.boost.updates.data.remote

import com.boost.upgrades.data.api_model.customerId.create.CustomerIDRequest
import com.boost.upgrades.data.api_model.customerId.create.CustomerIdResponse
import com.boost.upgrades.data.api_model.PurchaseOrder.CreatePurchaseOrderRequest
import com.boost.upgrades.data.api_model.PurchaseOrder.CreatePurchaseOrderResponse
import com.boost.upgrades.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.upgrades.data.api_model.customerId.get.GetCustomerIDResponse
import com.boost.upgrades.data.model.UpdatesModel
import io.reactivex.Observable
import retrofit2.http.*

interface ApiInterface {

    @Headers("Content-Type: application/json")
    @GET("api/v1/upgradeData")
    fun getUpdates(@Query("start") start: String): Observable<List<UpdatesModel>>

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
    @POST("5e709c6230000087647a3019")
    fun CreatePurchaseOrder(@Body createPurchaseOrderRequest: CreatePurchaseOrderRequest): Observable<CreatePurchaseOrderResponse>
}