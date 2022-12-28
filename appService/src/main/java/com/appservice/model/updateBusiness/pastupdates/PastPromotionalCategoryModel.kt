package com.appservice.model.updateBusiness.pastupdates

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem

class PastPromotionalCategoryModel(
    val name: String,
    val id: String,
    var isSelected: Boolean = false
) : AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.PAST_TAGS.getLayout()
    }
}