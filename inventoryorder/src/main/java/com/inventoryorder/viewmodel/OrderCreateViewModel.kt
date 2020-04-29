package com.inventoryorder.viewmodel

import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.rest.repositories.SellerOrderRepository

class OrderCreateViewModel : BaseViewModel() {

  fun getSellerSummary(sellerId: String?): LiveData<BaseResponse> {
    return SellerOrderRepository.getSellerSummary(sellerId).toLiveData()
  }

  fun getSellerAllOrder(request: OrderSummaryRequest): LiveData<BaseResponse> {
    return SellerOrderRepository.getSellerAllOrder(request).toLiveData()
  }

}