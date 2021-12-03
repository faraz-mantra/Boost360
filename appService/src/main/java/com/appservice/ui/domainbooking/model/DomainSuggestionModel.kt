package com.appservice.ui.domainbooking.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem

class DomainSuggestionModel(
    var domainName:String
): AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return RecyclerViewItemType.DOMAIN_NAME_SUGGESTIONS.getLayout()
    }
}
