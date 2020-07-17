package com.boost.upgrades.data.renewalcart


import com.google.gson.annotations.SerializedName

data class Expiry(
    @SerializedName("Key")
    var key: String? = null,
    @SerializedName("Value")
    var value: Int? = null
) {
  fun valueDays(): Int {
    return value ?: 30
  }

}