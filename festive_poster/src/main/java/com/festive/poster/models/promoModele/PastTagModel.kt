package com.festive.poster.models.promoModele

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

class PastTagModel(
    val description: String,
    val icon: String,
    val name: String,
    val tag: String,
    var isSelected: Boolean = false

) : AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.PAST_TAGS.getLayout()
    }
}