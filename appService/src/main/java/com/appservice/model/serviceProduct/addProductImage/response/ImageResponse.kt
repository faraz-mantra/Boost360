package com.appservice.model.serviceProduct.addProductImage.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ImageResponse(
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("url")
    var url: String? = null
) : Serializable