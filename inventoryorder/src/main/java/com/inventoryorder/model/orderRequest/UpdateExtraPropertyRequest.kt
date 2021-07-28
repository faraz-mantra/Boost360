package com.inventoryorder.model.orderRequest

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpdateExtraPropertyRequest(
  @SerializedName("existingKeyName")
  var existingKeyName: String? = null,
  @SerializedName("extraProperties")
  var extraProperties: ExtraProperties? = null,
  @SerializedName("orderId")
  var orderId: String? = null,
  @SerializedName("updateExtraPropertyType")
  var updateExtraPropertyType: String? = PropertyType.ITEM.name
) : BaseRequest(), Serializable {

  enum class PropertyType {
    ITEM, SELLER, BUYER, PAYMENT, DELIVERY, BILLING, SETTLEMENT, REFUND, CANCELLATION;
  }
}