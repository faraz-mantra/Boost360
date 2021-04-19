package com.boost.presignin.rest.userprofile

import com.google.gson.annotations.SerializedName

data class UserProfileVerificationResponse(
        @SerializedName("Error")
        val Error: UserError,
        @SerializedName("Result")
        val Result: VerificationRequestResult,
        @SerializedName("StatusCode")
        val StatusCode: Int,
)