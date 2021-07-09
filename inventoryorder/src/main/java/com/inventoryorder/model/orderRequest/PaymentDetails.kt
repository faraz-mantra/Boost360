package com.inventoryorder.model.orderRequest

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PaymentDetails(
    @SerializedName("Method")
    var method: String? = null,
    @SerializedName("Status")
    var status : String?= null
) : Serializable {

}