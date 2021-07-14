package com.inventoryorder.model.spaAppointment

import com.framework.base.BaseResponse
import java.io.Serializable

data class GetServiceListingResponse(
  var Error: Any? = null,
  var Result: Result? = null,
  var StatusCode: Int? = null
) : BaseResponse(), Serializable