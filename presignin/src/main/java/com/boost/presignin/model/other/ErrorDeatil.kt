package com.boost.presignin.model.other

import com.google.gson.annotations.SerializedName

data class ErrorDeatil(
        @SerializedName("ErrorCode")
        var errorCode: Any? = null,
        @SerializedName("ErrorList")
        var errorList: ErrorDetailList? = null
)