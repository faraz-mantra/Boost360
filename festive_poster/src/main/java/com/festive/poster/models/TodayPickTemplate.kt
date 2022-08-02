package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

class TodayPickTemplate(
    id: String,
    isFavourite: Boolean,
    name: String,
    primarySvgUrl: String,
    primaryText: String,
    tags: List<String>,
    utilizationDate: Any?
):TemplateUi(id,isFavourite,name,primarySvgUrl,primaryText,tags,utilizationDate),AppBaseRecyclerViewItem{
    override fun getViewType(): Int {
        return RecyclerViewItemType.TEMPLATE_VIEW_FOR_TODAY_PICK.getLayout()
    }

}