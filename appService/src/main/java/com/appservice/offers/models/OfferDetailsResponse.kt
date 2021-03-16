package com.appservice.offers.models

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class OfferDetailsResponse(

        @field:SerializedName("StatusCode")
        val statusCode: Int? = null,

        @field:SerializedName("Result")
        val result: OfferModel? = null,
) : BaseResponse()






