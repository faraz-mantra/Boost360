package com.appservice.model.kycData.kycList


import com.appservice.model.kycData.KycExtra
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class PaymentKycListResponse(
    @SerializedName("Extra")
    var extra: KycExtra? = null,
    @SerializedName("WebActions")
    var webActions: List<WebActionKyc>? = null
) : BaseResponse()