package com.boost.dbcenterapi.data.api_model.paymentprofile


import com.google.gson.annotations.SerializedName

class ErrorList(
    @SerializedName("Message")
    var Message: String = "",
    @SerializedName("StackTrace")
    var StackTrace: String = ""

)