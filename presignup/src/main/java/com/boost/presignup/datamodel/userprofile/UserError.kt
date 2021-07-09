package com.boost.presignup.datamodel.userprofile

import com.google.gson.annotations.SerializedName

data class UserError(
        @SerializedName("ErrorCode")
        val ErrorCode: Any,
        @SerializedName("ErrorList")
        val ErrorList: Any
)