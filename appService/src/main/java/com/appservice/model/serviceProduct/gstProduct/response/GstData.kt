package com.appservice.model.serviceProduct.gstProduct.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GstData(
    @SerializedName("ActionId")
    var actionId: String? = null,
    @SerializedName("CreatedOn")
    var createdOn: String? = null,
    @SerializedName("gst_slab")
    var gstSlab: Double? = null,
    @SerializedName("height")
    var height: Double? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("IsArchived")
    var isArchived: Boolean? = null,
    @SerializedName("length")
    var length: Double? = null,
    @SerializedName("merchant_id")
    var merchantId: String? = null,
    @SerializedName("product_id")
    var productId: String? = null,
    @SerializedName("UpdatedOn")
    var updatedOn: String? = null,
    @SerializedName("UserId")
    var userId: String? = null,
    @SerializedName("WebsiteId")
    var websiteId: String? = null,
    @SerializedName("weight")
    var weight: Double? = null,
    @SerializedName("width")
    var width: Double? = null
) : Serializable