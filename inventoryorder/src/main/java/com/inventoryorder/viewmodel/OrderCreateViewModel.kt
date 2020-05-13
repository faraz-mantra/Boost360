package com.inventoryorder.viewmodel

import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.rest.repositories.SellerOrderRepository

class OrderCreateViewModel : BaseViewModel() {

  fun getSellerSummary(clientId: String?, sellerId: String?): LiveData<BaseResponse> {
    return SellerOrderRepository.getSellerSummary(clientId, sellerId).toLiveData()
  }

  fun getSellerAllOrder(auth: String, request: OrderSummaryRequest): LiveData<BaseResponse> {
    return SellerOrderRepository.getSellerAllOrder(auth, request).toLiveData()
  }

}