package com.appservice.model.generalApt

import com.framework.base.BaseResponse
import java.io.Serializable

data class GeneralAptResponse(
  var Error: Any? = null,
  var Result: GeneralAptResult? = null,
  var StatusCode: Int? = null
) : BaseResponse(), Serializable