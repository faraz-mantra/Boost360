package com.appservice.model.businessmodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BusinessProfileUpdateRequest {

    @SerializedName("clientId")
    @Expose
    var clientId: String? = null

    @SerializedName("fpTag")
    @Expose
    var fpTag: String? = null

    @SerializedName("updates")
    @Expose
    var updates: List<Update>? = null

}