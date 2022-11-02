package com.boost.dbcenterapi.data.renewalcart


import com.google.gson.annotations.SerializedName

data class Widget(
  @SerializedName("WidgetId")
  var widgetId: String? = null,
  @SerializedName("WidgetKey")
  var widgetKey: String? = null
)