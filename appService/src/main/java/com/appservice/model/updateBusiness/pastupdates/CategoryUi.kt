package com.appservice.model.updateBusiness.pastupdates

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class CategoryUi(
    val iconUrl: String,
    val id: String,
    val name: String,
    val description:String,
    val thumbnailUrl: String,
):Parcelable{


}


fun List<CategoryUi>.asPastPromotionalCategoryModels()=map {
    PastPromotionalCategoryModel(
        id = it.id,
        name = it.name
    )
}

