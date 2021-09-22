package com.inventoryorder.model.orderRequest

import com.framework.utils.roundTo
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.math.roundToInt

data class ProductDetails(
  @SerializedName("CurrencyCode")
  var currencyCode: String? = null,
  @SerializedName("Description")
  var description: String? = null,
  @SerializedName("DiscountAmount")
  var discountAmount: Double? = null,
  @SerializedName("ExtraProperties")
  var extraProperties: ExtraProperties? = null,
  @SerializedName("_id")
  var id: String? = null,
  @SerializedName("IsAvailable")
  var isAvailable: Boolean? = null,
  @SerializedName("Name")
  var name: String? = null,
  @SerializedName("Price")
  var price: Double? = null,
  @SerializedName("ShippingCost")
  var shippingCost: Double? = null,
  @SerializedName("ImageUri")
  var imageUri: String? = null,
) : Serializable {

  fun getActualPrice(): Double {
    return price ?: 0.0
  }

  fun getDiscountAmount(): Double {
    return discountAmount ?: 0.0
  }

  fun getPayablePrice(): Double {
    return if (getActualPrice() >= getDiscountAmount()) getActualPrice() - getDiscountAmount() else getActualPrice()
  }

  fun getDiscountPercentage(): Double {
    return if (getActualPrice() >= getDiscountAmount()) (((getDiscountAmount() / getActualPrice()) * 100).roundTo(
      1
    )) else 0.0
  }

  fun getShapingCost(): Double {
    return shippingCost ?: 0.0
  }

  fun getCurrencyCodeValue(): String {
    return if (currencyCode.isNullOrEmpty().not()) currencyCode!! else "INR"
  }
}