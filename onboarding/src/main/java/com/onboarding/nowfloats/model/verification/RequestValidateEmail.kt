package com.onboarding.nowfloats.model.verification

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName

data class RequestValidateEmail(

    @field:SerializedName("clientId")
    var clientId: String? = null,

    @field:SerializedName("email")
    var email: String? = null,
) : BaseRequest()
