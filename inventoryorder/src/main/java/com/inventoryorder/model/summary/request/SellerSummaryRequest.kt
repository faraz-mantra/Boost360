package com.inventoryorder.model.summary.request


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName

data class SellerSummaryRequest(
  @SerializedName("filterBy")
  var filterBy: ArrayList<FilterBy>? = null
):BaseRequest()