package com.dashboard.model.live.customerItem


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CustomerItem(
    @SerializedName("action_item")
  var actionItem: ArrayList<CustomerActionItem>? = null,
    @SerializedName("type")
  var type: String? = null
): Serializable