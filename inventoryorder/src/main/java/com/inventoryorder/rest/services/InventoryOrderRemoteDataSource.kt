package com.inventoryorder.rest.services

import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.SendMailRequest
import com.inventoryorder.model.doctorsData.DoctorDataResponse
import com.inventoryorder.model.orderRequest.feedback.FeedbackRequest
import com.inventoryorder.model.orderRequest.paymentRequest.PaymentReceivedRequest
import com.inventoryorder.model.orderRequest.shippedRequest.MarkAsShippedRequest
import com.inventoryorder.model.orderfilter.OrderFilterRequest
import com.inventoryorder.model.summary.request.SellerSummaryRequest
import com.inventoryorder.rest.EndPoints
import com.inventoryorder.rest.response.OrderSummaryResponse
import com.inventoryorder.rest.response.order.InventoryOrderListResponse
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.rest.response.order.ProductResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface InventoryOrderRemoteDataSource {

  @GET(EndPoints.GET_SELLER_SUMMARY_URL)
  fun getSellerSummary(
      @Query("clientId") clientId: String?,
      @Query("sellerId") sellerId: String?,
  ): Observable<Response<OrderSummaryResponse>>

  @POST(EndPoints.GET_SELLER_SUMMARY_V2_5_URL)
  fun getSellerSummaryV2_5(
      @Query("clientId") clientId: String?,
      @Query("sellerId") sellerId: String?,
      @Body request: SellerSummaryRequest?,
  ): Observable<Response<OrderSummaryResponse>>


  @GET(EndPoints.GET_LIST_ORDER_URL)
  fun getSellerOrders(
      @Query("clientId") clientId: String?,
      @Query("sellerId") sellerId: String?,
      @Query("orderMode") orderMode: String?,
      @Query("deliveryMode") deliveryMode: String?,
      @Query("orderStatus") orderStatus: String?,
      @Query("paymentStatus") paymentStatus: String?,
      @Query("skip") skip: Int?,
      @Query("limit") limit: Int?,
  ): Observable<Response<InventoryOrderListResponse>>

  @POST(EndPoints.GET_LIST_ORDER_FILTER_URL)
  fun getSellerOrdersFiler(
      @Query("clientId") clientId: String?,
      @Body request: OrderFilterRequest,
  ): Observable<Response<InventoryOrderListResponse>>

  @GET(EndPoints.GET_LIST_ASSURE_PURCHASE_ORDER)
  fun getAssurePurchaseOrders(
      @Query("clientId") clientId: String?,
      @Query("sellerId") sellerId: String?,
      @Query("skip") skip: Int?,
      @Query("limit") limit: Int?,
  ): Observable<Response<InventoryOrderListResponse>>

  @GET(EndPoints.GET_LIST_CANCELLED_ORDER)
  fun getCancelledOrders(
      @Query("clientId") clientId: String?,
      @Query("sellerId") sellerId: String?,
      @Query("skip") skip: Int?,
      @Query("limit") limit: Int?,
  ): Observable<Response<InventoryOrderListResponse>>

  @GET(EndPoints.GET_ORDER_DETAIL)
  fun getOrderDetails(
      @Query("clientId") clientId: String?,
      @Query("orderId") orderId: String?,
  ): Observable<Response<OrderDetailResponse>>

  @GET(EndPoints.GET_PRODUCT_DETAILS)
  fun getProductDetails(@Query("productId") productId: String?): Observable<Response<ProductResponse>>

  @GET(EndPoints.GET_LIST_IN_COMPLETE_ORDER)
  fun getInCompleteOrders(
      @Query("clientId") clientId: String?,
      @Query("sellerId") sellerId: String?,
      @Query("skip") skip: Int?,
      @Query("limit") limit: Int?,
  ): Observable<Response<InventoryOrderListResponse>>


  @GET(EndPoints.GET_CONFIRM_ORDER)
  fun confirmOrder(
      @Query("clientId") clientId: String?,
      @Query("orderId") orderId: String?,
  ): Observable<Response<OrderConfirmStatus>>

  @GET(EndPoints.GET_CANCEL_ORDER)
  fun cancelOrder(
      @Query("clientId") clientId: String?,
      @Query("orderId") orderId: String?,
      @Query("cancellingEntity") cancellingEntity: String?,
  ): Observable<Response<OrderConfirmStatus>>

  @GET(EndPoints.MARK_AS_DELIVERED)
  fun markAsDelivered(
      @Query("clientId") clientId: String?,
      @Query("orderId") orderId: String?,
  ): Observable<Response<ResponseBody>>

  @GET(EndPoints.MARK_COD_PAYMENT_DONE)
  fun markCodPaymentDone(
      @Query("clientId") clientId: String?,
      @Query("orderId") orderId: String?,
  ): Observable<Response<ResponseBody>>

  @POST(EndPoints.MARK_PAYMENT_RECEIVED_MERCHANT)
  fun markPaymentReceivedMerchant(
      @Query("clientId") clientId: String?,
      @Body request: PaymentReceivedRequest?,
  ): Observable<Response<ResponseBody>>

  @GET(EndPoints.SEND_PAYMENT_REMINDER)
  fun sendPaymentReminder(
      @Query("clientId") clientId: String?,
      @Query("orderId") orderId: String?,
  ): Observable<Response<ResponseBody>>

  @GET(EndPoints.ORDER_RE_BOOKING_REQUEST)
  fun sendReBookingReminder(
      @Query("clientId") clientId: String?,
      @Query("orderId") orderId: String?,
  ): Observable<Response<ResponseBody>>

  @POST(EndPoints.ORDER_FEEDBACK_REQUEST)
  fun sendOrderFeedbackRequest(
      @Query("clientId") clientId: String?,
      @Body request: FeedbackRequest?,
  ): Observable<Response<ResponseBody>>

  @POST(EndPoints.MARK_AS_SHIPPED)
  fun markAsShipped(
      @Query("clientId") clientId: String?,
      @Body request: MarkAsShippedRequest?,
  ): Observable<Response<ResponseBody>>

  @GET(EndPoints.GET_DOCTORS_API)
  fun getDoctorsData(
      @Query("websiteID") fpTag: String?,
  ): Observable<Response<ArrayList<DoctorDataResponse>>>

  @POST(EndPoints.SEND_MAIL)
  fun sendMail(@HeaderMap headers: Map<String, String>, @Body request: SendMailRequest?): Observable<Response<ResponseBody>>
}