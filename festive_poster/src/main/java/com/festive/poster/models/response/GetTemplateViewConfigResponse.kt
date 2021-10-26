package com.festive.poster.models.response

import com.festive.poster.models.GetTemplateViewConfigResult
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetTemplateViewConfigResponse(
    @SerializedName("Result")
    val Result: GetTemplateViewConfigResult,
):BaseResponse()