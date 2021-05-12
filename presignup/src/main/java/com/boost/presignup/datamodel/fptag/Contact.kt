package com.boost.presignup.datamodel.fptag


import com.google.gson.annotations.SerializedName

data class Contact(
  @SerializedName("ContactName")
  var contactName: String? = null,
  @SerializedName("ContactNumber")
  var contactNumber: String? = null
)