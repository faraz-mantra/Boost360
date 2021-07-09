package com.onboarding.nowfloats.model.profile


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MerchantChannels(
    @SerializedName("EMAIL")
    var eMAIL: Boolean? = null,
    @SerializedName("FACEBOOK")
    var fACEBOOK: Boolean? = null,
    @SerializedName("GOOGLE")
    var gOOGLE: Boolean? = null,
    @SerializedName("OTP")
    var oTP: Boolean? = null,
) : Serializable