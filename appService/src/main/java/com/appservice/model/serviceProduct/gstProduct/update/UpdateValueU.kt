package com.appservice.model.serviceProduct.gstProduct.update


import com.google.gson.annotations.SerializedName

data class UpdateValueU(
    @SerializedName("$".plus("set"))
    var `set`: SetGST? = null
)