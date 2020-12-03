package com.inventoryorder.model.summary

import com.framework.utils.getNumberFormat
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SummaryEntity(
    @SerializedName("NoOfMessages")
    var noOfMessages: Int? = null,
    @SerializedName("NoOfSubscribers")
    var noOfSubscribers: Int? = null,
    @SerializedName("NoOfUniqueViews")
    var noOfUniqueViews: Int? = null,
    @SerializedName("NoOfViews")
    var noOfViews: Int? = null,
) : Serializable {

  fun getNoOfMessages(): String {
    return getNumberFormat((noOfMessages ?: 0).toString())
  }

  fun getNoOfSubscribers(): String {
    return getNumberFormat((noOfSubscribers ?: 0).toString())
  }

  fun getNoOfUniqueViews(): String {
    return getNumberFormat((noOfUniqueViews ?: 0).toString())
  }

  fun getNoOfViews(): String {
    return getNumberFormat((noOfViews ?: 0).toString())
  }
}