package com.festive.poster.models.response

data class GetTemplatesV2Body(
    val floatingPointId: String,
    val floatingPointTag: String,
    val categoryId: String?=null,
    val showFavourites: Boolean?=null,
    val tags: List<String>?=null
)