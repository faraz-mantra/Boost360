package com.boost.presignin.model.plan

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import com.onboarding.nowfloats.model.plan.ExtraNProperty

data class Plan15DaysResponseItem(
  @SerializedName("extraProperties")
  var extraProperties: ArrayList<ExtraNProperty>? = null,
  @SerializedName("planName")
  var planName: String? = null,
  @SerializedName("widgetKeys")
  var widgetKeys: ArrayList<String>? = null
) : BaseResponse()