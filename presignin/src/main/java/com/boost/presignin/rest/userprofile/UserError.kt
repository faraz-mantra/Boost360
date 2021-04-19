package com.boost.presignin.rest.userprofile

import com.google.gson.annotations.SerializedName

data class UserError(
        @SerializedName("ErrorCode")
        val ErrorCode: Any,
        @SerializedName("ErrorList")
        val ErrorList: Any,
)