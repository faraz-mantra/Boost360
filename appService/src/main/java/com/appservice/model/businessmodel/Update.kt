package com.appservice.model.businessmodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Update {
    @SerializedName("key")
    @Expose
    var key: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null
}