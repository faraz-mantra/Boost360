package com.appservice.model.generalApt

import com.framework.base.BaseRequest
import java.io.Serializable

data class UpdateRequestGeneralApt(
  var currency: String? = "INR",
  var description: String? = "General Appointment",
  var duration: Int? = null,
  var floatingPointTag: String? = null,
  var isAvailable: Boolean? = null,
  var name: String? = "General Appointment",
  var price: Double? = null
) : BaseRequest(), Serializable