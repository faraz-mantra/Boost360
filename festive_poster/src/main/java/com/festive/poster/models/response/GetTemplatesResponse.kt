package com.festive.poster.models.response

import com.festive.poster.models.TemplateUi
import com.framework.base.BaseResponse

data class GetTemplatesResponse(
    val Error: Any,
    val Result: GetTemplatesResponseResult,
    val StatusCode: Int
):BaseResponse()

data class GetTemplatesResponseResult(
    val count: Int,
    val templates: List<GetTemplatesResponseTemplate>
)

data class GetTemplatesResponseTemplate(
    val categories: List<GetCategoryResponseResult>,
    val id: String,
    val importanceIndex: Double,
    val isFavourite: Boolean,
    val isFeatured: Boolean,
    val name: String,
    val primarySvgUrl: String,
    val primaryText: String,
    val secondarySvgUrls: List<Any>,
    val tags: List<String>,
    val utilizationDate: String?
)

fun List<GetTemplatesResponseTemplate>.asDomainModels(): List<TemplateUi> {
    return map {
        TemplateUi(
            id = it.id,
            isFavourite = it.isFavourite,
            name = it.name,
            primarySvgUrl = it.primarySvgUrl,
            primaryText = it.primaryText,
            tags = it.tags,
            utilizationDate = it.utilizationDate
        )
    }
}