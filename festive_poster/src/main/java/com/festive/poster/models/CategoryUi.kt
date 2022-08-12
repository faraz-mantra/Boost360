package com.festive.poster.models

import android.os.Parcelable
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.models.promoModele.PastPromotionalCategoryModel
import com.festive.poster.models.response.Category
import com.festive.poster.models.response.FavCategory
import com.festive.poster.models.response.GetCategoryResponseResult
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import kotlinx.android.parcel.Parcelize

@Parcelize
open class CategoryUi(
    val iconUrl: String,
    val id: String,
    val name: String,
    val thumbnailUrl: String,
    private var templates:List<TemplateUi>?=null,
):Parcelable{
    fun setTemplates(templates: List<TemplateUi>?){
        this.templates=templates
    }

    fun getParentTemplates(): List<TemplateUi>? {
        return templates
    }

}


fun List<CategoryUi>.asPastPromotionalCategoryModels()=map {
    PastPromotionalCategoryModel(
        id = it.id,
        name = it.name
    )
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
            _id = it.id,
            _iconUrl = it.iconUrl,
            _name = it.name,
            _thumbnailUrl = it.thumbnailUrl,
            isSelected = false,
            templates = it.getParentTemplates()?.asBrowseAllModels()
        )
    }
}

fun List<CategoryUi>.asTodaysPickModels(): List<TodaysPickCategory> {
    return map {
        TodaysPickCategory(
            id = it.id,
            iconUrl = it.iconUrl,
            name = it.name,
            thumbnailUrl = it.thumbnailUrl,
            templates = it.getParentTemplates()?.asTodaysPickModels()
        )
    }
}

fun List<CategoryUi>.asFavModels(): List<FavCategory> {
    return map {
        FavCategory(
            _id = it.id,
            _iconUrl = it.iconUrl,
            _name = it.name,
            _thumbnailUrl = it.thumbnailUrl,
            isSelected = false,
            templates = it.getParentTemplates()?.asFavModels()
        )
    }
}
