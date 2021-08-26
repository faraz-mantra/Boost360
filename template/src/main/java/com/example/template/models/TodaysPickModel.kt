package com.example.template.models

import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.example.template.R

data class TodaysPickModel(
    var cat_title:String?=null,
    var cat_icon:String?=null,
    var cat_desc:String?=null
):AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.list_item_todays_pick_template
    }
}