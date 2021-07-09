package com.appservice.model.paymentKyc


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PaymentKycRequest(
    @SerializedName("ActionData")
    var actionData: ActionDataKyc? = null,
    @SerializedName("WebsiteId")
    var websiteId: String? = null
) : BaseRequest(), Serializable