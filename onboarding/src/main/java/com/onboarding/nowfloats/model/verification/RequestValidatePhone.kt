package com.onboarding.nowfloats.model.verification

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName

data class RequestValidatePhone(

  @field:SerializedName("clientId")
  var clientId: String? = null,

  @field:SerializedName("countryCode")
  var countryCode: String? = null,

  @field:SerializedName("mobile")
  var mobile: String? = null
) : BaseRequest()
