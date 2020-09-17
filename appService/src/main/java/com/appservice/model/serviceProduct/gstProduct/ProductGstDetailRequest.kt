package com.appservice.model.serviceProduct.gstProduct


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductGstDetailRequest(
    @SerializedName("ActionData")
    var actionData: ActionDataG? = null,
    @SerializedName("WebsiteId")
    var websiteId: String? = null
) : BaseRequest(), Serializable