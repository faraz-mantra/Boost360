package com.festive.poster.models.response

import com.festive.poster.models.CategoryUi
import com.festive.poster.models.TemplateUi
import com.framework.base.BaseResponse

data class GetTodayTemplateResponse(
    val Error: Any,
    val Result: List<GetTodayTemplateResponseResult>,
    val StatusCode: Int
):BaseResponse()

data class GetTodayTemplateResponseResult(
    val category: CategoryResponse,
    val templates: List<GetTodayTemplateResponseTemplate>
)

data class GetTodayTemplateResponseTemplate(
    val id: String,
    val importanceIndex: Double,
    val isFavourite: Boolean,
    val isFeatured: Boolean,
    val name: String,
    val primarySvgUrl: String,
    val primaryText: String,
    val secondarySvgUrls: List<Any>,
    val tags: List<String>,
    val utilizationDate: String?,
    val favDate:Long
)

fun List<GetTodayTemplateResponseTemplate>.asDomainModels(categoryId: String): List<TemplateUi> {
    return map {
        TemplateUi(
            id = it.id,
            isFavourite = it.isFavourite,
            name = it.name,
            primarySvgUrl = it.primarySvgUrl,
            primaryText = it.primaryText,
            tags = it.tags,
            utilizationDate = it.utilizationDate,
            categoryId = categoryId,
            favDate = it.favDate,
            isFeatured = it.isFeatured
        )
    }
}
fun List<GetTodayTemplateResponseResult>.asDomainModel(sortByDate:Boolean=false): List<CategoryUi> {
    return map {
       val templates = it.templates.asDomainModels(it.category.id)
        CategoryUi(
            id = it.category.id,
            name = it.category.name,
            thumbnailUrl = it.category.thumbnailUrl,
            templates = templates,
            iconUrl = it.category.iconUrl,
            description = it.category.description
        )
    }
}