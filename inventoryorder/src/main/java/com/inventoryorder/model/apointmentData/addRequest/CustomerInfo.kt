package com.inventoryorder.model.apointmentData.addRequest


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CustomerInfo(
  @SerializedName("EmailId")
  var emailId: String? = null,
  @SerializedName("MobileNumber")
  var mobileNumber: String? = null,
  @SerializedName("Name")
  var name: String? = null,
  @SerializedName("Address")
  var address: String? = null
):Serializable