package com.inventoryorder.model.apointmentData.updateRequest

import com.framework.utils.convertObjToString
import com.google.gson.annotations.SerializedName
import com.inventoryorder.model.apointmentData.addRequest.CustomerInfo
import java.io.Serializable

data class SetField(
  @SerializedName("bookingRef")
  var bookingRef: String? = null,
  @SerializedName("customerInfo")
  var customerInfo: String? = null,
  @SerializedName("dateTimeSlot")
  var dateTimeSlot: String? = null,
  @SerializedName("doctorId")
  var doctorId: String? = null,
  @SerializedName("notes")
  var notes: String? = "",
  @SerializedName("serviceId")
  var serviceId: String? = null
) : Serializable {

  fun setCustomerInfo(info: CustomerInfo) {
    this.customerInfo = convertObjToString(info)
  }
}