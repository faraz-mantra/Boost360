package com.festive.poster.models.promoModele

import com.festive.poster.R
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

data class TodaysPickModel(
    var cat_title:String?=null,
    var cat_icon:String?=null,
    var cat_desc:String?=null,
    var templateList:ArrayList<TemplateModel>?=null

): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.list_item_todays_pick_template
    }
}