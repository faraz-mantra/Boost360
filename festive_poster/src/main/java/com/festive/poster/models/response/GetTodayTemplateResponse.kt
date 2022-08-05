package com.festive.poster.models.response

import com.festive.poster.models.CategoryUi
import com.framework.base.BaseResponse

data class GetTodayTemplateResponse(
    val Error: Any,
    val Result: List<GetTodayTemplateResponseResult>,
    val StatusCode: Int
):BaseResponse()

data class GetTodayTemplateResponseResult(
    val category: Category,
    val templates: List<GetTemplatesResponseV2Template>
)

fun List<GetTodayTemplateResponseResult>.asDomainModel(): List<CategoryUi> {
    return map {
        CategoryUi(
            id = it.category.id,
            name = it.category.name,
            thumbnailUrl = it.category.thumbnailUrl,
            templates = it.templates.asDomainModels(),
            iconUrl = it.category.iconUrl
        )
    }
}