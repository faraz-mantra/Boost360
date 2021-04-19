package com.boost.presignin.rest.userprofile

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserProfileResponse(
        @SerializedName("Error")
        val Error: UserError,
        @SerializedName("Result")
        val Result: UserResult,
        @SerializedName("StatusCode")
        val StatusCode: Int,
) : Serializable, BaseResponse()