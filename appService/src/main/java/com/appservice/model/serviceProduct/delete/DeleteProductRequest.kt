package com.appservice.model.serviceProduct.delete


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DeleteProductRequest(
    @SerializedName("clientId")
    var clientId: String? = null,
    @SerializedName("identifierType")
    var identifierType: String? = null,
    @SerializedName("productId")
    var productId: String? = null,
    @SerializedName("productType")
    var productType: String? = null
) : BaseRequest(), Serializable