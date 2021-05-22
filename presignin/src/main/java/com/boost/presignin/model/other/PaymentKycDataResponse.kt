package com.boost.presignin.model.other

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PaymentKycDataResponse(
    @SerializedName("Data")
    var data: ArrayList<DataKyc>? = null,
    @SerializedName("Extra")
    var extra: KycExtra? = null,
) : BaseResponse(), Serializable