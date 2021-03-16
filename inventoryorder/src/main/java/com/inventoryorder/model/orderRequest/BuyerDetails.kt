package com.inventoryorder.model.orderRequest

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BuyerDetails(
    @SerializedName("ContactDetails")
    var contactDetails: ContactDetails,
    @SerializedName("GSTIN")
    var GSTIN: String = "",
    @SerializedName("Address")
    var address: Address,
) : Serializable