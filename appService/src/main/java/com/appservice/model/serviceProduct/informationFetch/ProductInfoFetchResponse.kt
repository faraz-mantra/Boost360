package com.appservice.model.serviceProduct.informationFetch


import com.google.gson.annotations.SerializedName

data class ProductInfoFetchResponse(
    @SerializedName("Data")
    var `data`: DataInfo? = null,
    @SerializedName("Message")
    var message: String? = null,
    @SerializedName("Status")
    var status: String? = null
)