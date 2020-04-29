package com.inventoryorder.rest.services

import com.inventoryorder.rest.EndPoints
import com.inventoryorder.rest.response.SellerSummaryResponse
import com.inventoryorder.rest.response.order.SellerOrderListResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SellerOrderRemoteDataSource {

  @GET(EndPoints.GET_SELLER_SUMMARY_URL)
  fun getSellerSummary(@Query("sellerId") sellerId: String?): Observable<Response<SellerSummaryResponse>>

  @GET(EndPoints.GET_LIST_ORDER_URL)
  fun getListOrder(@Query("sellerId") sellerId: String?,
                   @Query("orderStatus") orderStatus: String?,
                   @Query("skip") skip: Int?,
                   @Query("limit") limit: Int?): Observable<Response<SellerOrderListResponse>>
}