package com.inventoryorder.rest.services

import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AssuredPurchaseDataSource {

  @POST(EndPoints.POST_INITIATE_ORDER)
  fun initiateOrder(
      @Query("clientId") clientId: String?,
      @Body request: OrderInitiateRequest?
  ): Observable<Response<OrderInitiateResponse>>

}