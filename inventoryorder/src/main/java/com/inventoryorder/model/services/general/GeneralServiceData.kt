package com.inventoryorder.model.services.general

import java.io.Serializable

data class GeneralServiceData(
  val Currency: String? = null,
  val Description: String? = null,
  val DiscountAmount: Double? = null,
  val Duration: Int? = null,
  val GstSlab: Double? = null,
  val IsAvailable: Boolean? = null,
  val IsPriceInclusiveOfTax: Boolean? = null,
  val Name: String? = null,
  val Price: Double? = null,
  val Timings: List<Any>? = null,
  val _id: String
) : Serializable{

  fun getDiscountedPrice():Double{
    return Price?:0.0.minus(DiscountAmount?:0.0)
  }
}