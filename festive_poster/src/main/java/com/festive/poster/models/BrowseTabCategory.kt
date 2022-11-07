package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

class BrowseTabCategory(
    iconUrl: String,
    id: String,
    name: String,
    thumbnailUrl: String,
    description:String,
    val templates:List<BrowseAllTemplate>?=null,
):CategoryUi(iconUrl,id,name,description,thumbnailUrl,templates), AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.BROWSE_TAB_TEMPLATE_CAT.getLayout()
    }

}