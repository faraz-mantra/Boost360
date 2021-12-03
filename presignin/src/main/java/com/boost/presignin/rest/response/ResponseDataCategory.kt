package com.boost.presignin.rest.response

import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.model.category.CategoryDataModelOv2
import com.framework.base.BaseResponse

class ResponseDataCategory(
  val data: List<CategoryDataModel>? = null
) : BaseResponse()

class ResponseDataCategoryOv2(
  val data: List<CategoryDataModelOv2>? = null
) : BaseResponse()