package com.appservice.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem

data class ImageData(
    var url:String,
    var layout:Int) : AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return layout
    }
}
