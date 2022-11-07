package com.appservice.model.generalApt

import java.io.Serializable

data class GeneralAptResult(
  var Currency: String? = null,
  var Description: String? = null,
  var DiscountAmount: Double? = null,
  var Duration: Int? = null,
  var GstSlab: Double? = null,
  var IsAvailable: Boolean? = null,
  var IsPriceInclusiveOfTax: Boolean? = null,
  var Name: String? = null,
  var Price: Double? = null,
  var Timings: List<Any>? = null,
  var _id: String? = null
) : Serializable