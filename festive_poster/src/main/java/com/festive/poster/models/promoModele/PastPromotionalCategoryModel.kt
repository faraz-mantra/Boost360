package com.festive.poster.models.promoModele

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

class PastPromotionalCategoryModel(
    val name: String,
    val id: String,
    var isSelected: Boolean = false
) : AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.PAST_TAGS.getLayout()
    }
}