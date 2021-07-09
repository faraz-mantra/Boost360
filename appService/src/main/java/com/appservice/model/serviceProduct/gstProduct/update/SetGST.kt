package com.appservice.model.serviceProduct.gstProduct.update


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SetGST(
    @SerializedName("gst_slab")
    var gstSlab: String? = null,
    @SerializedName("height")
    var height: String? = null,
    @SerializedName("length")
    var length: String? = null,
    @SerializedName("weight")
    var weight: String? = null,
    @SerializedName("width")
    var width: String? = null
): Serializable