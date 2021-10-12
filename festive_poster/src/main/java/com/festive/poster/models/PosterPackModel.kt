package com.festive.poster.models

import com.festive.poster.R
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem


open class PosterPackModel(
    val tagsModel: PosterPackTagModel,
    var posterList:ArrayList<PosterModel>?=null,
    var price:Double=0.0,
    var isPurchased:Boolean=false
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.list_item_poster_pack
    }
}