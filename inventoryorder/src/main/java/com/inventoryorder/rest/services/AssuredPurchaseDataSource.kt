package com.inventoryorder.rest.services

import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.UpdateOrderNPropertyRequest
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.model.orderRequest.UpdateExtraPropertyRequest
import com.inventoryorder.rest.EndPoints
import com.inventoryorder.rest.response.order.OrderDetailResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AssuredPurchaseDataSource {

  @POST(EndPoints.POST_INITIATE_ORDER)
  fun initiateOrder(
    @Query("clientId") clientId: String?,
    @Body request: OrderInitiateRequest?
  ): Observable<Response<OrderInitiateResponse>>

  @POST(EndPoints.POST_UPDATE_ORDER)
  fun updateOrder(
    @Query("clientId") clientId: String?,
    @Body request: OrderInitiateRequest?
  ): Observable<Response<Any>>


  @POST(EndPoints.POST_UPDATE_EXTRA_FIELD_ORDER)
  fun updateExtraPropertyOrder(
    @Query("clientId") clientId: String?,
    @Body request: UpdateExtraPropertyRequest?
  ): Observable<Response<Any>>

  @POST(EndPoints.POST_UPDATE_EXTRA_FIELD_ORDER)
  fun updateExtraPropertyCancelOrder(
    @Query("clientId") clientId: String?,
    @Body request: UpdateOrderNPropertyRequest?
  ): Observable<Response<Any>>

  @POST(EndPoints.POST_INITIATE_APPOINTMENT)
  fun initiateAppointment(
    @Query("clientId") clientId: String?,
    @Body request: OrderInitiateRequest?
  ): Observable<Response<OrderInitiateResponse>>

  @GET(EndPoints.GET_CONFIRM_ORDER_2_5)
  fun confirmOrder(
    @Query("clientId") clientId: String?,
    @Query("orderId") orderId: String?,
  ): Observable<Response<OrderConfirmStatus>>

  @GET(EndPoints.GET_ORDER_DETAIL_2_5)
  fun getOrderDetailsV2_5(
    @Query("clientId") clientId: String?,
    @Query("orderId") orderId: String?,
  ): Observable<Response<OrderDetailResponse>>
}