package com.inventoryorder.model

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import com.inventoryorder.model.orderRequest.extraProperty.ExtraPropertiesOrder
import java.io.Serializable

data class UpdateOrderNPropertyRequest(
    @SerializedName("existingKeyName")
    var existingKeyName: String? = null,
    @SerializedName("orderId")
    var orderId: String? = null,
    @SerializedName("updateExtraPropertyType")
    var updateExtraPropertyType: String? = PropertyType.ITEM.name,
    @SerializedName("extraProperties")
    var extraPropertiesOrder: ExtraPropertiesOrder? = null,
) : BaseRequest(), Serializable {

  enum class PropertyType {
    ITEM, SELLER, BUYER, PAYMENT, DELIVERY, BILLING, SETTLEMENT, REFUND, CANCELLATION;
  }
}