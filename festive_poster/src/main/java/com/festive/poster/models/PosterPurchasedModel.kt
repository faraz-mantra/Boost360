package com.festive.poster.models

import com.festive.poster.R
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem


class PosterPurchasedModel(
    imageUrl:String?
): AppBaseRecyclerViewItem, PosterModel(imageUrl) {
    override fun getViewType(): Int {
        return R.layout.list_item_purchased_poster
    }
}