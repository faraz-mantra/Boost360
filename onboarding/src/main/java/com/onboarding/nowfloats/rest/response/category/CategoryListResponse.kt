package com.onboarding.nowfloats.rest.response.category

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.category.CategoryModel

class CategoryListResponse(
        var data: ArrayList<CategoryModel> = ArrayList()
) : BaseResponse()
