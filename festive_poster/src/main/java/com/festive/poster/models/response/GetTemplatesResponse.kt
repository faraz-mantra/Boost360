package com.festive.poster.models.response

import com.festive.poster.models.GetTemplatesResult
import com.framework.base.BaseResponse

data class GetTemplatesResponse(
    val Result: GetTemplatesResult,
):BaseResponse()