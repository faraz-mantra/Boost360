package com.festive.poster.models

import com.festive.poster.R
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem


class PosterPackDownloadsModel(
    title:String?=null,
    price:Int?=null,
    posterList:ArrayList<PosterModel>?=null
): AppBaseRecyclerViewItem, PosterPackModel(title,price,posterList) {
    override fun getViewType(): Int {
        return R.layout.list_item_downloads_poster
    }
}