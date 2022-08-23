package com.festive.poster.models.response

data class Template(
    val id: String,
    val importanceIndex: Double,
    val isFavourite: Boolean,
    val isFeatured: Boolean,
    val name: String,
    val primarySvgUrl: String,
    val primaryText: String,
    val secondarySvgUrls: List<Any>,
    val tags: List<String>,
    val utilizationDate: Any
)