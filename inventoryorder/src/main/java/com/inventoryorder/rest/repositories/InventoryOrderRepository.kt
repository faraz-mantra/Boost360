package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.rest.Taskcode
import com.inventoryorder.rest.apiClients.WithFloatsApiClient
import com.inventoryorder.rest.services.InventoryOrderRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object InventoryOrderRepository : AppBaseRepository<InventoryOrderRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<InventoryOrderRemoteDataSource> {
    return InventoryOrderRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun getSellerSummary(clientId: String?, sellerId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getSellerSummary(clientId, sellerId), Taskcode.GET_SELLER_SUMMARY)
  }

  fun getSellerAllOrder(auth: String, request: OrderSummaryRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getListOrder(auth, request.clientId, request.sellerId, request.orderStatus, request.skip, request.limit), Taskcode.GET_LIST_ORDER)
  }

  fun confirmOrder(clientId: String?, orderId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getConfirmOrder(clientId, orderId), Taskcode.CONFIRM_ORDER_TASK)
  }

  fun cancelOrder(clientId: String?, orderId: String?, cancellingEntity: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getCancelOrder(clientId, orderId, cancellingEntity), Taskcode.CANCEL_ORDER_TASK)
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }
}