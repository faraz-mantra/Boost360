package com.inventoryorder.model.spaAppointment.bookingslot.response

import com.framework.base.BaseResponse
import java.io.Serializable

data class BookingSlotResponse(
  var Error: Any? = null,
  var Result: List<Result>? = null,
  var StatusCode: Int? = null
) : BaseResponse(), Serializable