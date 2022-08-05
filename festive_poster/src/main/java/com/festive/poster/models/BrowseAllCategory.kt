package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.models.response.GetCategoryResponseResult
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

class BrowseAllCategory(
    iconUrl: String,
    id: String,
    name: String,
    thumbnailUrl: String,
    val templates:List<BrowseAllTemplate>?=null,
    var isSelected:Boolean=false
):CategoryUi(iconUrl,id,name,thumbnailUrl,templates), AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()
    }

}

