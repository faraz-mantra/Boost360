package com.festive.poster.models

import com.festive.poster.R
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem


open class PosterModel(
    var imgUrl:String?=null,
    var Keys:ArrayList<KeyModel>?=null
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.list_item_poster
    }
}