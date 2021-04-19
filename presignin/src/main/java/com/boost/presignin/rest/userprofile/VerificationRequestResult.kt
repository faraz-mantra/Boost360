package com.boost.presignin.rest.userprofile

import com.google.gson.annotations.SerializedName

data class VerificationRequestResult(
        @SerializedName("loginId")
        val loginId: String? = null,
        @SerializedName("ValidFPIds")
        val ValidFPIds: Array<String>? = null,
        @SerializedName("isEnterprise")
        val isEnterprise: Boolean? = null,
        @SerializedName("sourceClientId")
        val sourceClientId: String? = null,
        @SerializedName("profileProperties")
        val profileProperties: ProfileProperties? = null,
)