package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.models.response.GetCategoryResponseResult
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import kotlinx.android.parcel.Parcelize

@Parcelize
class BrowseAllCategory(
    private val _iconUrl: String,
    private val _id: String,
    private val _name: String,
    private val _thumbnailUrl: String,
    private val _description:String,
    val templates:List<BrowseAllTemplate>?=null,
    var isSelected:Boolean=false
):CategoryUi(_iconUrl,_id,_name,_description,_thumbnailUrl,templates), AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()
    }

}

