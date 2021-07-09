package com.appservice.model.account.response


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountCreateResponse(
    @SerializedName("Error")
    var errorN: AccountError? = null,
    @SerializedName("Result")
    var result: Any? = null,
    @SerializedName("StatusCode")
    var statusCode: Int? = null
) : BaseResponse(), Serializable