package com.appservice.model.kycData.kycList


import com.appservice.model.kycData.Extra
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class PaymentKycListResponse(
    @SerializedName("Extra")
    var extra: Extra? = null,
    @SerializedName("WebActions")
    var webActions: List<WebActionKyc>? = null
) : BaseResponse()