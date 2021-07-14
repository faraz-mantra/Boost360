package com.inventoryorder.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import com.inventoryorder.model.ordersdetails.OrderItem
import java.io.Serializable

data class OrderInitiateResponse(
  @SerializedName("Status")
  val statusN: String = "",
  @SerializedName("Message")
  val messageN: String = "",
  @SerializedName("Data")
  val data: OrderItem
) : BaseResponse(), Serializable