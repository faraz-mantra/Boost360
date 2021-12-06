package com.boost.presignin.model

import com.boost.presignin.constant.RecyclerViewItemType
import com.boost.presignin.recyclerView.AppBaseRecyclerViewItem

class CategorySuggestionUiModel(
    val category:String,
    val subCategory:String,
    val appExpCode:String,
    var searchKeyword:String,
):AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
       return RecyclerViewItemType.CATEGORY_SUGGESTION_ITEM.getLayout()
    }
}