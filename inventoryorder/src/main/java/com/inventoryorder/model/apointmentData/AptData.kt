package com.inventoryorder.model.apointmentData

import com.framework.utils.convertStringToObj
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AptData(
  @SerializedName("ActionId")
  var actionId: String? = null,
  @SerializedName("bookingRef")
  var bookingRef: Any? = null,
  @SerializedName("category")
  var category: String? = null,
  @SerializedName("CreatedOn")
  var createdOn: String? = null,
  @SerializedName("customerInfo")
  var customerInfo: String? = null,
  @SerializedName("dateTimeSlot")
  var dateTimeSlot: String? = null,
  @SerializedName("doctorId")
  var doctorId: String? = null,
  @SerializedName("_id")
  var id: String? = null,
  @SerializedName("IsArchived")
  var isArchived: Boolean? = null,
  @SerializedName("notes")
  var notes: String? = null,
  @SerializedName("serviceId")
  var serviceId: String? = null,
  @SerializedName("status")
  var status: String? = null,
  @SerializedName("UpdatedOn")
  var updatedOn: String? = null,
  @SerializedName("UserId")
  var userId: String? = null,
  @SerializedName("WebsiteId")
  var websiteId: String? = null
) : Serializable {

  fun getCustomerInfoData(): CustomerInfoData? {
    return if (customerInfo.isNullOrEmpty().not()) convertStringToObj(customerInfo!!) else null
  }
}