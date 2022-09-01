package com.festive.poster.models.response

data class GetTemplatesBody(
    val floatingPointId: String,
    val floatingPointTag: String,
    val categoryId: String?=null,
    val showFavourites: Boolean?=null,
    val tags: List<String>?=null
)