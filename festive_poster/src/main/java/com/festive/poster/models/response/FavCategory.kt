package com.festive.poster.models.response

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.models.CategoryUi
import com.festive.poster.models.FavTemplate
import com.festive.poster.models.response.GetCategoryResponseResult
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import kotlinx.android.parcel.Parcelize


@Parcelize
class FavCategory(
    private val _iconUrl: String,
    private val _id: String,
    private val _name: String,
    private val _thumbnailUrl: String,
    val templates:List<FavTemplate>?=null,
    var isSelected:Boolean=false
): CategoryUi(_iconUrl,_id,_name,_thumbnailUrl,templates), AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.FAV_CAT.getLayout()
    }

}

