package com.appservice.model.kycData


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class PaymentKycDataResponse(
    @SerializedName("Data")
    var `data`: List<DataKyc>? = null,
    @SerializedName("Extra")
    var extra: Extra? = null
) : BaseResponse()