package com.festive.poster.models.response

import com.festive.poster.models.GetTemplateViewConfigResult
import com.framework.base.BaseResponse

data class GetTemplateViewConfigResponse(
    val Result: GetTemplateViewConfigResult,
):BaseResponse()