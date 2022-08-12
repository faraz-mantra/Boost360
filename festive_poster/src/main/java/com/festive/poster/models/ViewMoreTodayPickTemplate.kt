package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import kotlinx.android.parcel.Parcelize

@Parcelize
class ViewMoreTodayPickTemplate(
    val categorySize:Int
):TodayPickTemplate("",false,"","",
    "", arrayListOf(),null,""), AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.VIEW_MORE_POSTER.getLayout()
    }

}