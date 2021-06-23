package com.boost.presignin.model.login

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ChannelProfileProperties(
    @SerializedName("userEmail")
    val userEmail: String,
    @SerializedName("userMobile")
    val userMobile: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userPassword")
    val userPassword: String
):Serializable