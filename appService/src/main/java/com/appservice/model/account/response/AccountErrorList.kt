package com.appservice.model.account.response


import com.google.gson.annotations.SerializedName

data class AccountErrorList(
    @SerializedName("Message")
    var message: String? = null,
    @SerializedName("StackTrace")
    var stackTrace: String? = null,
    @SerializedName("INVALID PARAMETERS")
    var invalidParameter: String? = null
)