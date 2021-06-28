package com.boost.presignin.model.userprofile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileProperties(
    @SerializedName("userEmail")
    val userEmail: String,
    @SerializedName("userMobile")
    val userMobile: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userPassword")
    val userPassword: String,
):Serializable
