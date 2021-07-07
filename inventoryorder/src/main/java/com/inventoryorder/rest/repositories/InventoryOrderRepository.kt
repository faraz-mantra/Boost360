package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.model.orderRequest.feedback.FeedbackRequest
import com.inventoryorder.model.orderRequest.paymentRequest.PaymentReceivedRequest
import com.inventoryorder.model.orderRequest.shippedRequest.MarkAsShippedRequest
import com.inventoryorder.model.orderfilter.OrderFilterRequest
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.model.summary.request.SellerSummaryRequest
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.apiClients.AssuredPurchaseClient
import com.inventoryorder.rest.services.InventoryOrderRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object InventoryOrderRepository :
  AppBaseRepository<InventoryOrderRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<InventoryOrderRemoteDataSource> {
    return InventoryOrderRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun getSellerSummary(clientId: String?, sellerId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getSellerSummary(ORDER_CLIENT_ID_JIO, sellerId),
      TaskCode.GET_SELLER_SUMMARY
    )
  }

  fun getSellerSummaryV2_5(
    clientId: String?,
    sellerId: String?,
    request: SellerSummaryRequest?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getSellerSummaryV2_5(ORDER_CLIENT_ID_JIO, sellerId, request),
      TaskCode.GET_SELLER_SUMMARY
    )
  }

  fun getSellerOrders(request: OrderSummaryRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getSellerOrders(
        ORDER_CLIENT_ID_JIO,
        request.sellerId,
        request.orderMode,
        request.deliveryMode,
        request.orderStatus,
        request.paymentStatus,
        request.skip,
        request.limit
      ), TaskCode.GET_LIST_ORDER
    )
  }

  fun getSellerOrdersFilter(request: OrderFilterRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getSellerOrdersFiler(ORDER_CLIENT_ID_JIO, request),
      TaskCode.GET_LIST_ORDER_FILTER
    )
  }

  fun getAssurePurchaseOrders(request: OrderSummaryRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getAssurePurchaseOrders(
        ORDER_CLIENT_ID_JIO,
        request.sellerId,
        request.skip,
        request.limit
      ), TaskCode.GET_ASSURE_PURCHASE_ORDER
    )
  }

  fun getCancelledOrders(request: OrderSummaryRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getCancelledOrders(
        ORDER_CLIENT_ID_JIO,
        request.sellerId,
        request.skip,
        request.limit
      ), TaskCode.GET_LIST_CANCELLED_ORDER
    )
  }

  fun getOrderDetails(clientId: String?, orderId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getOrderDetails(ORDER_CLIENT_ID_JIO, orderId),
      TaskCode.GET_ORDER_DETAILS
    )
  }

  fun getInCompleteOrders(request: OrderSummaryRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getInCompleteOrders(
        ORDER_CLIENT_ID_JIO,
        request.sellerId,
        request.skip,
        request.limit
      ), TaskCode.GET_LIST_IN_COMPLETE_ORDER
    )
  }

  fun confirmOrder(clientId: String?, orderId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.confirmOrder(ORDER_CLIENT_ID_JIO, orderId),
      TaskCode.CONFIRM_ORDER_TASK
    )
  }


  fun sendPaymentReminder(clientId: String?, orderId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.sendPaymentReminder(clientId, orderId),
      TaskCode.SEND_LINK_ORDER_TASK
    )
  }

  fun sendReBookingReminder(clientId: String?, orderId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.sendReBookingReminder(ORDER_CLIENT_ID_JIO, orderId),
      TaskCode.SEND_RE_BOOKING_ORDER_TASK
    )
  }

  fun sendOrderFeedbackRequest(
    clientId: String?,
    request: FeedbackRequest?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.sendOrderFeedbackRequest(ORDER_CLIENT_ID_JIO, request),
      TaskCode.SEND_FEEDBACK_REQUEST
    )
  }

  fun cancelOrder(
    clientId: String?,
    orderId: String?,
    cancellingEntity: String?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.cancelOrder(ORDER_CLIENT_ID_JIO, orderId, cancellingEntity),
      TaskCode.CANCEL_ORDER_TASK
    )
  }

  fun markAsDelivered(clientId: String?, orderId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.markAsDelivered(ORDER_CLIENT_ID_JIO, orderId),
      TaskCode.DELIVERED_ORDER_TASK
    )
  }

  fun markCodPaymentDone(clientId: String?, orderId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.markCodPaymentDone(ORDER_CLIENT_ID_JIO, orderId),
      TaskCode.MARK_PAYMENT_DONE_TASK
    )
  }

  fun markPaymentReceivedMerchant(
    clientId: String?,
    request: PaymentReceivedRequest?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.markPaymentReceivedMerchant(ORDER_CLIENT_ID_JIO, request),
      TaskCode.MARK_PAYMENT_MERCHANT_TASK
    )
  }

  fun markAsShipped(clientId: String?, request: MarkAsShippedRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.markAsShipped(ORDER_CLIENT_ID_JIO, request),
      TaskCode.SHIPPED_ORDER_TASK
    )
  }

  override fun getApiClient(): Retrofit {
    return AssuredPurchaseClient.shared.retrofit
  }
}