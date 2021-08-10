package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.model.UpdateOrderNPropertyRequest
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.model.orderRequest.UpdateExtraPropertyRequest
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.apiClients.AssuredPurchaseClient
import com.inventoryorder.rest.services.AssuredPurchaseDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object AssuredPurchaseRepository :
  AppBaseRepository<AssuredPurchaseDataSource, AppBaseLocalService>() {

  fun postOrderInitiate(
    clientId: String?,
    request: OrderInitiateRequest?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.initiateOrder(clientId, request),
      TaskCode.POST_ORDER_INITIATE
    )
  }

  fun postAppointmentInitiate(
    clientId: String?,
    request: OrderInitiateRequest?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.initiateAppointment(clientId, request),
      TaskCode.POST_ORDER_INITIATE
    )
  }

  fun confirmOrder(clientId: String?, orderId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.confirmOrder(clientId, orderId),
      TaskCode.CONFIRM_ORDER_TASK
    )
  }

  fun getOrderDetails(clientId: String?, orderId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getOrderDetailsV2_5(clientId, orderId),
      TaskCode.CONFIRM_ORDER_TASK
    )
  }
  fun getOrderCount(clientId: String?, sellerId: String?,orderStatus:Int?,startDate: String?, endDate: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getOrdersCount(clientId, sellerId,orderStatus,startDate, endDate),
      TaskCode.GET_ALL_ORDERS
    )
  }

  fun postOrderUpdate(clientId: String?, request: OrderInitiateRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateOrder(clientId, request),
      TaskCode.POST_ORDER_UPDATE
    )
  }

  fun updateExtraPropertyOrder(
    clientId: String?,
    request: UpdateExtraPropertyRequest?,
    requestCancel: UpdateOrderNPropertyRequest? = null
  ): Observable<BaseResponse> {
    return if (request != null) makeRemoteRequest(
      remoteDataSource.updateExtraPropertyOrder(
        clientId,
        request
      ), TaskCode.POST_ORDER_EXTRA_FILED_UPDATE
    )
    else makeRemoteRequest(
      remoteDataSource.updateExtraPropertyCancelOrder(clientId, requestCancel),
      TaskCode.POST_ORDER_EXTRA_FILED_UPDATE
    )
  }

  override fun getRemoteDataSourceClass(): Class<AssuredPurchaseDataSource> {
    return AssuredPurchaseDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return AssuredPurchaseClient.shared.retrofit
  }
}