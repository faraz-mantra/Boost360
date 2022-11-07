package com.appservice.model.panGst

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GstDetails(
  @SerializedName("businessName")
  var businessName: String? = null,
  @SerializedName("businessRegister")
  var businessRegister: Boolean? = null,
  @SerializedName("gstIn")
  var gstIn: String? = null,
  @SerializedName("rcmApply")
  var rcmApply: Boolean? = null,
  @SerializedName("status")
  var status: String? = null
) : Serializable