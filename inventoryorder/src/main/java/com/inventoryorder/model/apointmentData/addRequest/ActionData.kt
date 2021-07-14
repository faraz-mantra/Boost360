package com.inventoryorder.model.apointmentData.addRequest


import com.framework.utils.convertObjToString
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ActionData(
  @SerializedName("bookingRef")
  var bookingRef: String? = null,
  @SerializedName("category")
  var category: String? = null,
  @SerializedName("customerInfo")
  var customerInfo: String? = null,
  @SerializedName("dateTimeSlot")
  var dateTimeSlot: String? = null,
  @SerializedName("doctorId")
  var doctorId: String? = null,
  @SerializedName("notes")
  var notes: String? = null,
  @SerializedName("serviceId")
  var serviceId: String? = null,
  @SerializedName("status")
  var status: String? = null
) : Serializable {

  fun setCustomerInfo(info: CustomerInfo) {
    this.customerInfo = convertObjToString(info)
  }
}