package com.appservice.model.updateBusiness.pastupdates

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem

class PastSocialModel(
    var socialPlatformName: String
) : AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.PAST_SOCIAL_ICON_LIST_ITEM.getLayout()
    }

}