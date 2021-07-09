package com.dashboard.model.live.addOns


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BusinessActionData(
    @SerializedName("action_item")
    var actionItem: ArrayList<AllBoostAddOnsData>? = null,
    @SerializedName("type")
    var type: String? = null,
) : Serializable