package com.dashboard.model.live.quickAction


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ActionData(
  @SerializedName("action_item")
  var actionItem: ArrayList<QuickActionItem>? = null,
  @SerializedName("type")
  var type: String? = null,
) : Serializable