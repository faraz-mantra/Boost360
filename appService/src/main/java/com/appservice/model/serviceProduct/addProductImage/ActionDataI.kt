package com.appservice.model.serviceProduct.addProductImage


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ActionDataI(
    @SerializedName("image")
    var image: ImageI? = null,
    @SerializedName("_pid")
    var pid: String? = null
) : Serializable