package com.boost.upgrades.data.api_model.customerId.create

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CustomerIDRequest(
    @SerializedName("ClientId")
    @Expose
    val ClientId: String,
    @SerializedName("DeviceType")
    @Expose
    val DeviceType: Int,
    @SerializedName("Email")
    @Expose
    val Email: String,
    @SerializedName("InternalSourceId")
    @Expose
    val InternalSourceId: String,
    @SerializedName("Name")
    @Expose
    val Name: String,
    @SerializedName("MobileNumber")
    @Expose
    val MobileNumber: String,
    @SerializedName("PaymentChannel")
    @Expose
    val PaymentChannel: Int
)
