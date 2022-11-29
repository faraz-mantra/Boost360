package com.festive.poster.models.promoModele

import com.festive.poster.R
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

data class SocialConnModel(
    var icon:String?=null,
    var content:String?=null
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.list_item_social_conn
    }
}