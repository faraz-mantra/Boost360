package com.onboarding.nowfloats.model.plan


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class Plan15DaysResponseItem(
  @SerializedName("extraProperties")
  var extraProperties:ArrayList<ExtraProperty>? = null,
  @SerializedName("planName")
  var planName: String? = null,
  @SerializedName("widgetKeys")
  var widgetKeys: ArrayList<String>? = null
):BaseResponse()