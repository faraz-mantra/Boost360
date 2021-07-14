package com.onboarding.nowfloats.rest.response.category

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.category.CategoryDataModel

class ResponseDataCategory(
  val data: List<CategoryDataModel>? = null
) : BaseResponse()