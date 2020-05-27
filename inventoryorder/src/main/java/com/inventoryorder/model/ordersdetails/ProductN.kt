package com.inventoryorder.model.ordersdetails

import com.framework.utils.convertStringToObj
import com.inventoryorder.model.consultation.ExtraItemProductConsultation
import java.io.Serializable

data class ProductN(
    val CurrencyCode: String? = null,
    val Description: String? = null,
    val Dimensions: DimensionsN? = null,
    val DiscountAmount: Double? = null,
//    val ExtraProperties: ExtraPropertiesN? = null,
    val ExtraProperties: Any? = null,
    val ImageUri: String? = null,
    val IsAvailable: Boolean? = null,
    val Name: String? = null,
    val Price: Double? = null,
    val SKU: String? = null,
    val ShippingCost: Double? = null,
    val _id: String? = null
) : Serializable {

  fun extraItemProductConsultation(): ExtraItemProductConsultation? {
    return ExtraProperties?.toString()?.let { convertStringToObj(it, ExtraItemProductConsultation::class.java) }
  }
}