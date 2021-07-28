package com.onboarding.nowfloats.model.business.purchasedOrder


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConsumptionConstraint(
  @SerializedName("MetricKey")
  var metricKey: String? = null,
  @SerializedName("MetricValue")
  var metricValue: Int? = null
) : Serializable