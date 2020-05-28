package com.onboarding.nowfloats.rest.response.feature

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.feature.FeatureModel

class FeatureListResponse(
        var data: ArrayList<FeatureModel> = ArrayList()
) : BaseResponse()
