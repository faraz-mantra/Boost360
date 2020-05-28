package com.boost.presignup.datamodel.userprofile

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
        @SerializedName("Error")
        val Error: Error,
        @SerializedName("Result")
        val Result: Result,
        @SerializedName("StatusCode")
        val StatusCode: Int
)