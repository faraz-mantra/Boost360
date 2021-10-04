package com.festive.poster.models

import com.festive.poster.R
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem


open class PosterPackModel(
    var title:String?=null,
    var price:Int?=null,
    var posterList:ArrayList<PosterModel>?=null
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.list_item_poster_pack
    }
}