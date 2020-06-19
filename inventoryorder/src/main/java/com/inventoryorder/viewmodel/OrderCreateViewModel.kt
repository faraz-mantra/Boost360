package com.inventoryorder.viewmodel

import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.inventoryorder.model.orderfilter.OrderFilterRequest
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.rest.repositories.InventoryOrderRepository
import com.inventoryorder.rest.repositories.ProductOrderRepository

class OrderCreateViewModel : BaseViewModel() {

  fun getSellerSummary(clientId: String?, sellerId: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.getSellerSummary(clientId, sellerId).toLiveData()
  }

  fun getSellerOrders(auth: String, request: OrderSummaryRequest): LiveData<BaseResponse> {
    return InventoryOrderRepository.getSellerOrders(auth, request).toLiveData()
  }

  fun getSellerOrdersFilter(auth: String, request: OrderFilterRequest): LiveData<BaseResponse> {
    return InventoryOrderRepository.getSellerOrdersFilter(auth, request).toLiveData()
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

  fun cancelOrder(clientId: String?, orderId: String?, cancellingEntity: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.cancelOrder(clientId, orderId, cancellingEntity).toLiveData()
  }

  fun getProductDetails(productId: String?): LiveData<BaseResponse> {
    return ProductOrderRepository.getProductDetails(productId).toLiveData()
  }

}