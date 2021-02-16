package com.boost.presignup.datamodel.userprofile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserProfileResponse(
    @SerializedName("Error")
    val Error: UserError,
    @SerializedName("Result")
    val Result: UserResult,
    @SerializedName("StatusCode")
    val StatusCode: Int,
) : Serializable