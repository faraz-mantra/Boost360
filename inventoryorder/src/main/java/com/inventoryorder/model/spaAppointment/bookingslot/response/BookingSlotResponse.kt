package com.inventoryorder.model.spaAppointment.bookingslot.response

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BookingSlotResponse(
  var Error: Any? = null,
  @SerializedName(value = "Result")
  var Result: List<ResultSlot>? = null,
  var StatusCode: Int? = null
) : BaseResponse(), Serializable