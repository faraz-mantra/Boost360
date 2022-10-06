package com.festive.poster.models.response

import com.festive.poster.models.GetPosterViewConfigResult
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetTemplateViewConfigResponse(
    @SerializedName("Result")
    val Result: GetPosterViewConfigResult,
):BaseResponse()