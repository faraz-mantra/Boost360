package com.festive.poster.models

import com.festive.poster.R
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem


open class PosterModel(
    val Active: Boolean,
    val CreatedOn: String,
    val Details: PosterDetailsModel,
    val Id: String,
    var Keys: List<PosterKeyModel>,
    val Tags: List<String>,
    val UpdatedOn: String,
    val Variants: List<PosterVariantModel>,
    val isPurchased:Boolean=false
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return if (isPurchased)
            R.layout.list_item_purchased_poster
        else
            R.layout.list_item_poster
    }
}