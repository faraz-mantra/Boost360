package com.appservice.model.serviceProduct.gstProduct.response


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductGstResponse(
  @SerializedName("Data")
  var `data`: ArrayList<GstData>? = null,
  @SerializedName("Extra")
  var extra: GstExtra? = null
) : BaseResponse(), Serializable