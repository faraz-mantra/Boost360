package com.festive.poster.models.response

import com.festive.poster.models.CategoryUi

data class CategoryResponse(
    val iconUrl: String,
    val id: String,
    val name: String,
    val description:String,
    val thumbnailUrl: String
)

