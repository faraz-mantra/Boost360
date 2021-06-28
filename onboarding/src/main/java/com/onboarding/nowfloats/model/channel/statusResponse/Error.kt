package com.onboarding.nowfloats.model.channel.statusResponse


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Error(
    @SerializedName("error_code")
    var errorCode: Int? = null,
    @SerializedName("message")
    var message: String? = null,
) : Serializable