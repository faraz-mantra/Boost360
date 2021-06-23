package com.boost.presignin.rest.response

import com.boost.presignin.model.category.CategoryDataModel
import com.framework.base.BaseResponse

class ResponseDataCategory(
        val data: List<CategoryDataModel>? = null
) : BaseResponse()