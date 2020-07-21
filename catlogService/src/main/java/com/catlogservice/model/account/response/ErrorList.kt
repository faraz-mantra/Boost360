package com.catlogservice.model.account.response


import com.google.gson.annotations.SerializedName

data class ErrorList(
    @SerializedName("Message")
    var message: String? = null,
    @SerializedName("StackTrace")
    var stackTrace: String? = null,
    @SerializedName("INVALID PARAMETERS")
    var invalidParameter: String? = null
)