package com.dashboard.model.live.customerItem


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BoostCustomerItemResponse(
  @SerializedName("data")
  var `data`: ArrayList<CustomerItem>? = null
) : BaseResponse(), Serializable