package com.festive.poster.models

import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

class FeaturePurchaseUiModel(
    val title:String,
    val desc:String,
    val price:Double?,
    val code:String,
    val isPack:Boolean,
    var isSelected:Boolean=false,
):AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.FEATURE_PURCHASE.getLayout()
    }
}