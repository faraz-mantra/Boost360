package com.boost.presignup.datamodel.userprofile

import com.google.gson.annotations.SerializedName

data class VerificationRequestResult(
        @SerializedName("LoginId")
        val LoginId: String,
        @SerializedName("ValidFPIds")
        val ValidFPIds: Array<String>,
        @SerializedName("isEnterprise")
        val isEnterprise: Boolean,
        @SerializedName("sourceClientId")
        val sourceClientId: String,
        @SerializedName("channelProfileProperties")
        val channelProfileProperties: ProfileProperties
)