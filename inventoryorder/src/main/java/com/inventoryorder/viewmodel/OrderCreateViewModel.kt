package com.inventoryorder.viewmodel

import GetStaffListingRequest
import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.inventoryorder.model.SendMailRequest
import com.inventoryorder.model.UpdateOrderNPropertyRequest
import com.inventoryorder.model.apointmentData.addRequest.AddAptConsultRequest
import com.inventoryorder.model.apointmentData.updateRequest.UpdateConsultRequest
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.model.orderRequest.UpdateExtraPropertyRequest
import com.inventoryorder.model.orderRequest.feedback.FeedbackRequest
import com.inventoryorder.model.orderRequest.paymentRequest.PaymentReceivedRequest
import com.inventoryorder.model.orderRequest.shippedRequest.MarkAsShippedRequest
import com.inventoryorder.model.orderfilter.OrderFilterRequest
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.model.services.ServiceListingRequest
import com.inventoryorder.model.spaAppointment.bookingslot.request.BookingSlotsRequest
import com.inventoryorder.rest.repositories.*
import retrofit2.http.Body

class OrderCreateViewModel : BaseViewModel() {

  fun getSellerSummary(clientId: String?, sellerId: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.getSellerSummary(clientId, sellerId).toLiveData()
  }

  fun getProductItems(fpTag: String?, clientId: String?, skipBy: Int?): LiveData<BaseResponse> {
    return ApiTwoWithFloatRepository.getProductList(fpTag, clientId, skipBy).toLiveData()
  }

  fun getSellerOrders(request: OrderSummaryRequest): LiveData<BaseResponse> {
    return InventoryOrderRepository.getSellerOrders(request).toLiveData()
  }

  fun getSellerOrdersFilter(request: OrderFilterRequest): LiveData<BaseResponse> {
    return InventoryOrderRepository.getSellerOrdersFilter(request).toLiveData()
  }

  fun getAssurePurchaseOrder(request: OrderSummaryRequest): LiveData<BaseResponse> {
    return InventoryOrderRepository.getAssurePurchaseOrders(request).toLiveData()
  }

  fun getCancelledOrders(request: OrderSummaryRequest): LiveData<BaseResponse> {
    return InventoryOrderRepository.getCancelledOrders(request).toLiveData()
  }

  fun getOrderDetails(clientId: String?, orderId: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.getOrderDetails(clientId, orderId).toLiveData()
  }

  fun getInCompleteOrders(request: OrderSummaryRequest): LiveData<BaseResponse> {
    return InventoryOrderRepository.getInCompleteOrders(request).toLiveData()
  }

  fun confirmOrder(clientId: String?, orderId: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.confirmOrder(clientId, orderId).toLiveData()
  }

  fun sendPaymentReminder(clientId: String?, orderId: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.sendPaymentReminder(clientId, orderId).toLiveData()
  }

  fun sendReBookingReminder(clientId: String?, orderId: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.sendReBookingReminder(clientId, orderId).toLiveData()
  }

  fun sendOrderFeedbackRequest(clientId: String?, request: FeedbackRequest?): LiveData<BaseResponse> {
    return InventoryOrderRepository.sendOrderFeedbackRequest(clientId, request).toLiveData()
  }

  fun cancelOrder(clientId: String?, orderId: String?, cancellingEntity: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.cancelOrder(clientId, orderId, cancellingEntity).toLiveData()
  }

  fun markAsDelivered(clientId: String?, orderId: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.markAsDelivered(clientId, orderId).toLiveData()
  }

  fun markCodPaymentDone(clientId: String?, orderId: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.markCodPaymentDone(clientId, orderId).toLiveData()
  }

  fun markPaymentReceivedMerchant(clientId: String?, request: PaymentReceivedRequest?): LiveData<BaseResponse> {
    return InventoryOrderRepository.markPaymentReceivedMerchant(clientId, request).toLiveData()
  }

  fun markAsShipped(clientId: String?, request: MarkAsShippedRequest?): LiveData<BaseResponse> {
    return InventoryOrderRepository.markAsShipped(clientId, request).toLiveData()
  }

  fun getProductDetails(productId: String?): LiveData<BaseResponse> {
    return ProductOrderRepository.getProductDetails(productId).toLiveData()
  }

  fun getAllServiceList(clientId: String?, skipBy: Int?, fpTag: String?, identifierType: String?): LiveData<BaseResponse> {
    return ApiTwoWithFloatRepository.getAllServiceList(clientId, skipBy, fpTag, identifierType).toLiveData()
  }

  fun getServiceListing(request: ServiceListingRequest): LiveData<BaseResponse> {
    return NowFloatsRepository.getServiceListing(request).toLiveData()
  }

  fun getGeneralService(fpTag: String?, fpId: String?): LiveData<BaseResponse> {
    return NowFloatsRepository.getGeneralService(fpTag,fpId).toLiveData()
  }

  fun getDoctorData(fpTag: String?): LiveData<BaseResponse> {
    return ProductOrderRepository.getDoctorData(fpTag).toLiveData()
  }

  fun postOrderInitiate(clientId: String?, request: OrderInitiateRequest?): LiveData<BaseResponse> {
    return AssuredPurchaseRepository.postOrderInitiate(clientId, request).toLiveData()
  }

  fun postAppointment(clientId: String?, request: OrderInitiateRequest?): LiveData<BaseResponse> {
    return AssuredPurchaseRepository.postAppointmentInitiate(clientId, request).toLiveData()
  }

  fun assuredPurchaseConfirmOrder(clientId: String?, orderId: String?): LiveData<BaseResponse> {
    return AssuredPurchaseRepository.confirmOrder(clientId, orderId).toLiveData()
  }

  fun assuredPurchaseGetOrderDetails(clientId: String?, orderId: String?): LiveData<BaseResponse> {
    return AssuredPurchaseRepository.getOrderDetails(clientId, orderId).toLiveData()
  }

  fun updateExtraPropertyOrder(clientId: String?, request: UpdateExtraPropertyRequest? = null, requestCancel: UpdateOrderNPropertyRequest? = null): LiveData<BaseResponse> {
    return AssuredPurchaseRepository.updateExtraPropertyOrder(clientId, request, requestCancel).toLiveData()
  }

  fun postOrderUpdate(clientId: String?, request: OrderInitiateRequest?): LiveData<BaseResponse> {
    return AssuredPurchaseRepository.postOrderUpdate(clientId, request).toLiveData()
  }

  fun getWeeklyScheduleList(auth: String?, query: String?, sort: String? = "{CreatedOn: -1}", limit: Int = 1000): LiveData<BaseResponse> {
    return WebActionBoostRepository.getWeekSchedule(auth, query, sort, limit).toLiveData()
  }

  fun getAllAptConsultDoctor(auth: String?, query: String?, sort: String? = "{CreatedOn: -1}", limit: Int = 1000): LiveData<BaseResponse> {
    return WebActionBoostRepository.getAllAptConsultDoctor(auth, query, sort, limit).toLiveData()
  }

  fun addAptConsultData(auth: String?, request: AddAptConsultRequest?): LiveData<BaseResponse> {
    return WebActionBoostRepository.addAptConsultData(auth, request).toLiveData()
  }

  fun updateAptConsultData(auth: String?, request: UpdateConsultRequest?): LiveData<BaseResponse> {
    return WebActionBoostRepository.updateAptConsultData(auth, request).toLiveData()
  }

  fun sendSMS(mobile: String?, message: String?, clientId: String?): LiveData<BaseResponse> {
    return ApiTwoWithFloatRepository.sendSMS(mobile, message, clientId).toLiveData()
  }

  fun sendMail(request: SendMailRequest?): LiveData<BaseResponse> {
    return ProductOrderRepository.sendMail(request).toLiveData()
  }

  fun getSearchListing(fpTag: String, fpId: String, searchString: String, offset: Int, limit: Int): LiveData<BaseResponse> {
    return NowFloatsRepository.getSearchListing(fpTag, fpId, searchString, offset, limit).toLiveData()
  }

  fun getBookingSlots(bookingSlotsRequest: BookingSlotsRequest): LiveData<BaseResponse> {
    return NowFloatsRepository.getBookingSlots(bookingSlotsRequest).toLiveData()
  }

  fun getBookingSlotsStaff(staffId:String?,bookingSlotsRequest: BookingSlotsRequest): LiveData<BaseResponse> {
    return NowFloatsRepository.getBookingSlotsStaff(staffId,bookingSlotsRequest).toLiveData()
  }

  fun getDoctorsListing(@Body request: GetStaffListingRequest?): LiveData<BaseResponse> {
    return NowFloatsRepository.getDoctorsListing(request = request).toLiveData()
  }
}