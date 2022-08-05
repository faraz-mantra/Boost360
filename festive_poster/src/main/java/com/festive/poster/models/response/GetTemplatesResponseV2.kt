package com.festive.poster.models.response

import com.festive.poster.models.TemplateUi
import com.framework.base.BaseResponse

data class GetTemplatesResponseV2(
    val Error: Any,
    val Result: GetTemplatesResponseV2Result,
    val StatusCode: Int
):BaseResponse()

data class GetTemplatesResponseV2Result(
    val count: Int,
    val templates: List<GetTemplatesResponseV2Template>
)

data class GetTemplatesResponseV2Template(
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

fun List<GetTemplatesResponseV2Template>.asDomainModels(): List<TemplateUi> {
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