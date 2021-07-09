package com.boost.presignin.model.other

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RegisteredBusinessAddress(
        @SerializedName("City")
        var city: String? = null,
        @SerializedName("Country")
        var country: String? = null,
        @SerializedName("Line1")
        var line1: String? = null,
        @SerializedName("Line2")
        var line2: String? = null,
        @SerializedName("State")
        var state: String? = null
) : Serializable