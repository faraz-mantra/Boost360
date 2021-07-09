package com.appservice.model.serviceProduct.gstProduct.update


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpdateValueU(
    @SerializedName("$".plus("set"))
    var `set`: SetGST? = null
): Serializable