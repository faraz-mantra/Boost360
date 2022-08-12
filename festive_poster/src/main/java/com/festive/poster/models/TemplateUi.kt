package com.festive.poster.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
open class TemplateUi(
    val id: String,
    var isFavourite: Boolean,
    val name: String,
    val primarySvgUrl: String,
    val primaryText: String,
    val tags: List<String>,
    val utilizationDate: String?,
    val categoryId:String
):Parcelable


fun List<TemplateUi>.asFavModels(): List<FavTemplate> {
    return map {
        FavTemplate(
            _id = it.id,
            _isFavourite = it.isFavourite,
            _name = it.name,
            _primaryText = it.primaryText,
            _primarySvgUrl = it.primarySvgUrl,
            _tags = it.tags,
            _utilizationDate = it.utilizationDate,
            _categoryId = it.categoryId
        )
    }
}
fun List<TemplateUi>.asBrowseAllModels(): List<BrowseAllTemplate> {
    return map {
        BrowseAllTemplate(
            _id = it.id,
            _isFavourite = it.isFavourite,
            _name = it.name,
            _primaryText = it.primaryText,
            _primarySvgUrl = it.primarySvgUrl,
            _tags = it.tags,
            _utilizationDate = it.utilizationDate,
            _categoryId = it.categoryId
        )
    }
}

fun List<TemplateUi>.asTodaysPickModels(): List<TodayPickTemplate> {
    return map {
        TodayPickTemplate(
            _id = it.id,
            _isFavourite = it.isFavourite,
            _name = it.name,
            _primaryText = it.primaryText,
            _primarySvgUrl = it.primarySvgUrl,
            _tags = it.tags,
            _utilizationDate = it.utilizationDate,
            _categoryId = it.categoryId
        )
    }
}

