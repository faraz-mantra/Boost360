package com.boost.presignin.model.other

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TaxDetails(
        @SerializedName("GSTDetails")
        var gSTDetails: GSTDetails? = null,
        @SerializedName("PANDetails")
        var pANDetails: PANDetails? = null,
        @SerializedName("TANDetails")
        var tANDetails: TANDetails? = null
): Serializable