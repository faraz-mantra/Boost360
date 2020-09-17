package com.inventoryorder.model


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendMailRequest(
    @SerializedName("emailAddress")
    var emailAddress: String? = null,
    @SerializedName("htmlBody")
    var htmlBody: String? = null,
    @SerializedName("subject")
    var subject: String? = null,
    @SerializedName("type")
    var type: String? = null
) : BaseRequest(), Serializable