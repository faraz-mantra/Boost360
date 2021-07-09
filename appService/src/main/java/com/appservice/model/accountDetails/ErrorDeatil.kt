package com.appservice.model.accountDetails


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ErrorDeatil(
    @SerializedName("ErrorCode")
    var errorCode: Any? = null,
    @SerializedName("ErrorList")
    var errorList: ErrorDetailList? = null
): Serializable