package com.appservice.model.account


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RegisteredBusinessContactDetailsN(
    @SerializedName("MerchantName")
    var merchantName: String? = null,
    @SerializedName("RegisteredBusinessCountryCode")
    var registeredBusinessCountryCode: String? = null,
    @SerializedName("RegisteredBusinessEmail")
    var registeredBusinessEmail: String? = null,
    @SerializedName("RegisteredBusinessMobile")
    var registeredBusinessMobile: String? = null,
) : Serializable