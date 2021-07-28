package com.inventoryorder.model.apointmentData


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CustomerInfoData(
  @SerializedName("Age")
  var age: Int? = null,
  @SerializedName("EmailId")
  var emailId: String? = null,
  @SerializedName("Gender")
  var gender: String? = null,
  @SerializedName("MobileNumber")
  var mobileNumber: String? = null,
  @SerializedName("Name")
  var name: String? = null
) : Serializable