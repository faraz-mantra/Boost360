package com.boost.presignin.model.other

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RegisteredBusinessContactDetails(
        @SerializedName("MerchantName")
        var merchantName: String? = null,
        @SerializedName("RegisteredBusinessCountryCode")
        var registeredBusinessCountryCode: String? = null,
        @SerializedName("RegisteredBusinessEmail")
        var registeredBusinessEmail: String? = null,
        @SerializedName("RegisteredBusinessMobile")
        var registeredBusinessMobile: String? = null
) : Serializable