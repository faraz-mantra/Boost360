package com.boost.presignin.model.activatepurchase


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConsumptionConstraint(
  @SerializedName("MetricKey")
  var metricKey: String? = null,
  @SerializedName("MetricValue")
  var metricValue: Int? = null
) : Serializable