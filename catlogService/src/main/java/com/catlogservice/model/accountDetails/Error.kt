package com.catlogservice.model.accountDetails


import com.google.gson.annotations.SerializedName

data class Error(
    @SerializedName("ErrorCode")
    var errorCode: Any? = null,
    @SerializedName("ErrorList")
    var errorList: ErrorList? = null
)