package com.appservice.ui.domainbooking.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem

class SimilarDomainSuggestionModel(
    var domainName: String,
    var isDomainSelected: Boolean
) : AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return RecyclerViewItemType.SIMILAR_DOMAIN_SUGGESTIONS.getLayout()
    }
}
