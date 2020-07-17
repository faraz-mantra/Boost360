package com.boost.upgrades.data.renewalcart


import com.google.gson.annotations.SerializedName

data class ConsumptionConstraint(
    @SerializedName("MetricKey")
    var metricKey: String? = null,
    @SerializedName("MetricValue")
    var metricValue: Double? = null
)