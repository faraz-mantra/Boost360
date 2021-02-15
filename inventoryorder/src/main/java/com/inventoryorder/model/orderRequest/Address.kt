package com.inventoryorder.model.orderRequest

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Address(
    @SerializedName("AddressLine1")
    val addressLine: String? = null
):Serializable