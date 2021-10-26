package com.appservice.offers.models

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class OfferBaseResponse(
        @field:SerializedName("Result")
        var Result: String?=null) : BaseResponse()
