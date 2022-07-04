package com.festive.poster.models.promoModele

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem

class PastSocialModel(
    var socialPlatformName: String
) : AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.PAST_SOCIAL_ICON_LIST_ITEM.getLayout()
    }

}