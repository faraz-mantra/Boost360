package com.dashboard.model.live.websiteItem

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebsiteData(
    @SerializedName("action_item")
    var actionItem: ArrayList<WebsiteActionItem>? = null,
    @SerializedName("type")
    var type: String? = null,
): Serializable