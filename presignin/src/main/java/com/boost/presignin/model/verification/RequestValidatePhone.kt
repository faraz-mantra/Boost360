package com.boost.presignin.model.verification

import com.google.gson.annotations.SerializedName

data class RequestValidatePhone(

  @field:SerializedName("clientId")
  var clientId: String? = null,

  @field:SerializedName("countryCode")
  var countryCode: String? = null,

  @field:SerializedName("mobile")
  var mobile: String? = null
)
