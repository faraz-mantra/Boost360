package com.dashboard.model.live.welcomeData


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WelcomeActionData(
    @SerializedName("action_item")
    var actionItem: ArrayList<WelcomeData>? = null,
    @SerializedName("type")
    var type: String? = null,
) : Serializable