package com.appservice.model.panGst

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PanGstResult(
  @SerializedName("gstDetails")
  var gstDetails: GstDetails? = null,
  @SerializedName("panDetails")
  var panDetails: PanDetails? = null
) : Serializable