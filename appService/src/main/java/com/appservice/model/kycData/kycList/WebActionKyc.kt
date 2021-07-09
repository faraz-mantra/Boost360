package com.appservice.model.kycData.kycList


import com.google.gson.annotations.SerializedName

data class WebActionKyc(
    @SerializedName("ActionId")
    var actionId: String? = null,
    @SerializedName("Description")
    var description: String? = null,
    @SerializedName("DisplayName")
    var displayName: String? = null,
    @SerializedName("Name")
    var name: String? = null,
    @SerializedName("Properties")
    var properties: List<PropertyKyc>? = null,
    @SerializedName("Type")
    var type: String? = null,
    @SerializedName("UpdatedOn")
    var updatedOn: String? = null,
    @SerializedName("UserId")
    var userId: String? = null,
    @SerializedName("UserName")
    var userName: Any? = null,
    @SerializedName("Visibility")
    var visibility: Int? = null
)