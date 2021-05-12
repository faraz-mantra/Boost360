package com.boost.presignup.datamodel.fptag


import com.google.gson.annotations.SerializedName

data class Timing(
  @SerializedName("From")
  var from: String? = null,
  @SerializedName("To")
  var to: String? = null
)