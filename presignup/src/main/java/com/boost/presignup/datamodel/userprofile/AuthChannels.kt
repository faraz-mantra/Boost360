package com.boost.presignup.datamodel.userprofile

import com.google.gson.annotations.SerializedName

data class AuthChannels (

        @SerializedName("FACEBOOK") val fACEBOOK : Boolean,
        @SerializedName("GOOGLE") val gOOGLE : Boolean,
        @SerializedName("EMAIL") val eMAIL : Boolean,
        @SerializedName("OTP") val oTP : Boolean
)