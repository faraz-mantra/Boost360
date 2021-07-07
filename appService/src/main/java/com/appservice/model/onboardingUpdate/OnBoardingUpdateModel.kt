package com.appservice.model.onboardingUpdate

import com.framework.base.BaseRequest
import java.io.Serializable

data class OnBoardingUpdateModel(
  var query: String? = null,
  var updateValue: String? = null,
  var Multi: Boolean = true,
) : BaseRequest(), Serializable {

  fun setData(fpTag: String, value: String) {
    this.query = String.format("{fptag:'%s'}", fpTag)
    this.updateValue = String.format("{\$set:{%s}}", value)
  }
}