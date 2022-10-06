package com.boost.dbcenterapi.data.api_model.getCouponResponse


import com.google.gson.annotations.SerializedName

data class GetCouponResponseItem(
  @SerializedName("Data")
  var `data`: List<Data>? = null
)