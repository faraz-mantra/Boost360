package com.festive.poster.models

import com.framework.base.BaseResponse

data class GetFavTemplateResponse(
    val Result: GetFavTemplateResponseResult?,
):BaseResponse()

data class GetFavTemplateResponseResult(
    val FavouriteTemplatesDetails: List<FavouriteTemplatesDetail>?
)

data class FavouriteTemplatesDetail(
    val count: Int,
    val tag: String,
    val tagIcon: String?,
    val tagName: String?,
    val templateDetails: List<PosterPackModel>?
)