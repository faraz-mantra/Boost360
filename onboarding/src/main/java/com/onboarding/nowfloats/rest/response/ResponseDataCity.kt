package com.onboarding.nowfloats.rest.response

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.CityDataModel

class ResponseDataCity(
  val data: List<CityDataModel>? = null
) : BaseResponse()