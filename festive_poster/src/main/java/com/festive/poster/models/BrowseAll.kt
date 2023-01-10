package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

class BrowseAll(
    val title: String?,
    val postersCount: Int,
) : AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.BROWSE_ALL_CATEGORIES.getLayout()
    }
}

