package com.appservice.model.paymentKyc.update


import com.google.gson.annotations.SerializedName

data class UpdateKycValue(
    @SerializedName("$".plus("set"))
    var `set`: KycSet? = null
)