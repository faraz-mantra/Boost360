package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

class TodaysPickCategory(
    iconUrl: String,
    id: String,
    name: String,
    thumbnailUrl: String,
    val templates:List<TodayPickTemplate>?=null,
):CategoryUi(iconUrl,id,name,thumbnailUrl,templates), AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()
    }

}