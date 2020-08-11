package com.appservice.model.pickUpAddress


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PickUpAddressResponse(
    @SerializedName("Data")
    var `data`: ArrayList<PickUpData>? = null,
    @SerializedName("Message")
    var messageNew: String? = null,
    @SerializedName("Status")
    var statusNew: String? = null
) : BaseResponse(), Serializable