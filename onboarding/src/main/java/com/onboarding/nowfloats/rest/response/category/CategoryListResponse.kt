package com.onboarding.nowfloats.rest.response.category

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.category.CategoryModel

@Deprecated("old")
class CategoryListResponse(
        var data: ArrayList<CategoryModel> = ArrayList()
) : BaseResponse()
