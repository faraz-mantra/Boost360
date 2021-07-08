package com.inventoryorder.model.weeklySchedule

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Extra(
  @SerializedName("TotalCount")
  val totalCount: Int = 0,
  @SerializedName("PageSize")
  val pageSize: Int = 0,
  @SerializedName("CurrentIndex")
  val currentIndex: Int = 0
) : Serializable