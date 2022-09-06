package com.appservice.model.updateBusiness.pastupdates

import com.framework.base.BaseResponse

data class GetCategoryResponse(
    val Error: Any,
    val Result: List<GetCategoryResponseResult>,
    val StatusCode: Int
):BaseResponse()

data class GetCategoryResponseResult(
    val iconUrl: String,
    val id: String,
    val name: String,
    val description:String,
    val thumbnailUrl: String
)

fun List<GetCategoryResponseResult>.asDomainModels(): List<CategoryUi> {
    return map {
        CategoryUi(
            id = it.id,
            iconUrl = it.iconUrl,
            name = it.name,
            thumbnailUrl = it.thumbnailUrl,
            description = it.description
        )
    }
}
