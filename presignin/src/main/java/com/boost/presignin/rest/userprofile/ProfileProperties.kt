package com.boost.presignin.rest.userprofile

import com.google.gson.annotations.SerializedName

data class ProfileProperties(
        @SerializedName("userEmail")
        val userEmail: String,
        @SerializedName("userMobile")
        val userMobile: String,
        @SerializedName("userName")
        val userName: String,
        @SerializedName("userPassword")
        val userPassword: String,
)
