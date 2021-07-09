package com.inventoryorder.model.services

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UniquePaymentUrl(
    @SerializedName("description")
    var description: Any? = null,
    @SerializedName("url")
    var url: Any? = null
) : Serializable