package com.festive.poster.models

open class TemplateUi(
    val id: String,
    val isFavourite: Boolean,
    val name: String,
    val primarySvgUrl: String,
    val primaryText: String,
    val tags: List<String>,
    val utilizationDate: Any?
)

fun List<TemplateUi>.asBrowseAllModels(): List<BrowseAllTemplate> {
    return map {
        BrowseAllTemplate(
            id = it.id,
            isFavourite = it.isFavourite,
            name = it.name,
            primaryText = it.primaryText,
            primarySvgUrl = it.primarySvgUrl,
            tags = it.tags,
            utilizationDate = it.utilizationDate
        )
    }
}

fun List<TemplateUi>.asTodaysPickModels(): List<TodayPickTemplate> {
    return map {
        TodayPickTemplate(
            id = it.id,
            isFavourite = it.isFavourite,
            name = it.name,
            primaryText = it.primaryText,
            primarySvgUrl = it.primarySvgUrl,
            tags = it.tags,
            utilizationDate = it.utilizationDate
        )
    }
}

