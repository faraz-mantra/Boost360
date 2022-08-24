package com.festive.poster.models.response

import com.festive.poster.models.GetFestivePosterResult
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class GetFestivePosterResponse(
    @SerializedName("Result")
    val Result: GetFestivePosterResult,
):BaseResponse()