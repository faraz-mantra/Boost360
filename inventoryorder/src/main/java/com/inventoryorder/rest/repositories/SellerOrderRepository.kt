package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.rest.Taskcode
import com.inventoryorder.rest.apiClients.WithFloatsApiClient
import com.inventoryorder.rest.services.SellerOrderRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object SellerOrderRepository : AppBaseRepository<SellerOrderRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<SellerOrderRemoteDataSource> {
    return SellerOrderRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun getSellerSummary(sellerId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getSellerSummary(sellerId), Taskcode.GET_SELLER_SUMMARY)
  }

  fun getSellerAllOrder(auth: String, request: OrderSummaryRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getListOrder(auth, request.sellerId, request.orderStatus, request.skip, request.limit), Taskcode.GET_LIST_ORDER)
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }
}