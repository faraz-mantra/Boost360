package com.boost.presignin.model.other

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class PaymentKycDataResponse(
        @SerializedName("Data")
        var `data`: List<DataKyc>? = null,
        @SerializedName("Extra")
        var extra: KycExtra? = null
) :BaseResponse()