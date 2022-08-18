package com.festive.poster.models.promoModele

import com.festive.poster.R
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

data class TemplateModel(
    var imgUrl:String?=null,
    var desc:String?=null
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.list_item_template_for_rv
    }
}