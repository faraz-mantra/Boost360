package com.appservice.model.serviceProduct

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BuyOnlineLink(
    @SerializedName("url",alternate = ["Url"])
    var url: String? = null,
    @SerializedName("description",alternate = ["Description"])
    var description: String? = null
) : Serializable