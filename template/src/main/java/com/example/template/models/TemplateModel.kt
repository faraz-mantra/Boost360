package com.example.template.models

import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.example.template.R

data class TemplateModel(
    var imgUrl:String?=null,
    var desc:String?=null
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.list_item_template
    }
}