package com.boost.presignup.datamodel.userprofile

import com.google.gson.annotations.SerializedName

data class UserProfileVerificationResponse(
    @SerializedName("Error")
        val Error: UserError,
    @SerializedName("Result")
        val Result: VerificationRequestResult,
    @SerializedName("StatusCode")
        val StatusCode: Int
)