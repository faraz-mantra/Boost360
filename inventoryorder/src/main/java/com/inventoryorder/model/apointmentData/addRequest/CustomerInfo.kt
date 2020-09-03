package com.inventoryorder.model.apointmentData.addRequest


import com.google.gson.annotations.SerializedName

data class CustomerInfo(
  @SerializedName("EmailId")
  var emailId: String? = null,
  @SerializedName("MobileNumber")
  var mobileNumber: String? = null,
  @SerializedName("Name")
  var name: String? = null
)