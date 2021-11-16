package com.boost.cart.data.api_model.stateCode


import com.google.gson.annotations.SerializedName

data class Data(
  @SerializedName("id")
  var id: String? = null,
  @SerializedName("state")
  var state: String? = null,
  @SerializedName("state_code")
  var stateCode: String? = null,
  @SerializedName("state_tin")
  var stateTin: String? = null
)