package com.boost.upgrades.data.renewalcart

import com.google.gson.annotations.SerializedName

data class CreateCartResult(
  @SerializedName("CartStateId")
  var cartStateId: String? = null
)