package com.onboarding.nowfloats.model.profile


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileError(
    @SerializedName("ErrorCode")
    var errorCode: Any? = null,
    @SerializedName("ErrorList")
    var errorList: Any? = null,
) : Serializable