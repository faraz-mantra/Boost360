package com.boost.payment.data.renewalcart

import com.google.gson.annotations.SerializedName

data class CreateCartResult(
  @SerializedName("CartStateId")
  var cartStateId: String? = null
)