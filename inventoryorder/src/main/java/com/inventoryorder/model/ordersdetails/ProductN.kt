package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class ProductN(
  val CurrencyCode: String? = null,
  val Description: String? = null,
  val Dimensions: DimensionsN? = null,
  val DiscountAmount: Double? = null,
  val ExtraProperties: ExtraPropertiesN? = null,
  val ImageUri: String? = null,
  val IsAvailable: Boolean? = null,
  val Name: String? = null,
  val Price: Double? = null,
  val SKU: String? = null,
  val ShippingCost: Double? = null,
  val _id: String? = null,
  val BrandName: String? = null,
  val BrandNamSecondaryImagesUri: Any? = null,
) : Serializable {

  fun extraItemProductConsultation(): ExtraPropertiesN? {
    return ExtraProperties
  }

  fun isAvailable(): Boolean {
    return IsAvailable ?: false
  }

  fun price(): Double {
    return Price ?: 0.0
  }

  fun discountAmount(): Double {
    return DiscountAmount ?: 0.0
  }


  fun getCurrencyCodeValue(): String? {
    return CurrencyCode
  }
}