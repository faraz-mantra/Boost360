package com.inventoryorder.model

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import com.inventoryorder.model.orderRequest.extraProperty.ExtraPropertiesCancel
import java.io.Serializable

data class UpdateCancelPropertyRequest(
    @SerializedName("existingKeyName")
    var existingKeyName: String? = null,
    @SerializedName("orderId")
    var orderId: String? = null,
    @SerializedName("updateExtraPropertyType")
    var updateExtraPropertyType: String? = PropertyType.ITEM.name,
    @SerializedName("extraProperties")
    var extraPropertiesCancel: ExtraPropertiesCancel? = null,
) : BaseRequest(), Serializable {

  enum class PropertyType {
    ITEM, SELLER, BUYER, PAYMENT, DELIVERY, BILLING, SETTLEMENT, REFUND, CANCELLATION;
  }
}