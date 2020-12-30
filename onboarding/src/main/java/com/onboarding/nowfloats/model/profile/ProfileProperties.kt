package com.onboarding.nowfloats.model.profile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileProperties(
    @SerializedName("userEmail")
    var userEmail: String? = null,
    @SerializedName("userMobile", alternate = ["PhoneNumber"])
    var userMobile: String? = null,
    @SerializedName("userName")
    var userName: String? = null,
    @SerializedName("userPassword")
    var userPassword: String? = null,
) : Serializable