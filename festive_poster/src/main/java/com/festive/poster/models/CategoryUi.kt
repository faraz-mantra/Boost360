package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.models.response.GetCategoryResponseResult
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

open class CategoryUi(
    val iconUrl: String,
    val id: String,
    val name: String,
    val thumbnailUrl: String,
    private var templates:List<TemplateUi>?=null,
){
    fun setTemplates(templates: List<TemplateUi>?){
        this.templates=templates
    }

    fun getParentTemplates(): List<TemplateUi>? {
        return templates
    }

}


fun List<CategoryUi>.asBrowseTabModels(): List<BrowseTabCategory> {
    return map {
        BrowseTabCategory(
            id = it.id,
            iconUrl = it.iconUrl,
            name = it.name,
            thumbnailUrl = it.thumbnailUrl,
            templates = it.getParentTemplates()?.asBrowseAllModels()
        )
    }
}

fun List<CategoryUi>.asBrowseAllModels(): List<BrowseAllCategory> {
    return map {
        BrowseAllCategory(
            id = it.id,
            iconUrl = it.iconUrl,
            name = it.name,
            thumbnailUrl = it.thumbnailUrl,
            isSelected = false,
            templates = it.getParentTemplates()?.asBrowseAllModels()
        )
    }
}
