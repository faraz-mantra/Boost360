package com.appservice.model.serviceProduct.addProductImage.response


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductImageResponse(
    @SerializedName("Data")
    var `data`: ArrayList<DataImage>? = null,
    @SerializedName("Extra")
    var extra: ExtraI? = null
) : BaseResponse(), Serializable