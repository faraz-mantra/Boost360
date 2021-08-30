package com.example.template.models

import com.example.template.R
import com.example.template.recyclerView.AppBaseRecyclerViewItem

data class SocialConnModel(
    var icon:String?=null,
    var content:String?=null
):AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return R.layout.list_item_social_conn
    }
}