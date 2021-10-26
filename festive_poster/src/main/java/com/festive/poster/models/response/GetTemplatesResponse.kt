package com.festive.poster.models.response

import com.festive.poster.models.GetTemplatesResult
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetTemplatesResponse(
    @SerializedName("Result")
    val Result: GetTemplatesResult,
):BaseResponse()