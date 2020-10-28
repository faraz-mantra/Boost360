package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class ChannelData(
    var title: String? = null,
    var icon: Int? = null,
    var recyclerViewItemType: Int = RecyclerViewItemType.CHANNEL_ITEM_VIEW.getLayout(),
    var isConnect: Boolean? = true
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getDataChannel(recyclerViewItemType: Int = RecyclerViewItemType.CHANNEL_ITEM_VIEW.getLayout()): ArrayList<ChannelData> {
    val list = ArrayList<ChannelData>()
    list.add(ChannelData(title = "Website", icon = R.drawable.ic_website_d, recyclerViewItemType))
    list.add(ChannelData(title = "Maps", icon = R.drawable.ic_google_maps_n, recyclerViewItemType))
    list.add(ChannelData(title = "Fb page", icon = R.drawable.ic_facebook_page_n, recyclerViewItemType))
    list.add(ChannelData(title = "WA profile", icon = R.drawable.ic_whatsapp_business_n, recyclerViewItemType))
    list.add(ChannelData(title = "Twitter", icon = R.drawable.ic_twitter_n, recyclerViewItemType, isConnect = false))
    list.add(ChannelData(title = "Fb profile", icon = R.drawable.ic_facebook_shop_n, recyclerViewItemType, isConnect = false))
    return list
  }

}