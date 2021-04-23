package com.dashboard.model.live.channel

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelsType

class ChannelStatusData(
    @SerializedName("account_type")
    var accountType: String? = null,
    @SerializedName("total_count")
    var totalCount: Int? = null,
    @SerializedName("is_connected")
    var isConnected: Boolean = false,
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.CHANNEL_STATUS_ITEM_VIEW.getLayout()
  }

  fun getIconConnected(): Int? {
    return when (isConnected) {
      true -> R.drawable.ic_facebook_page_n
      false -> R.drawable.ic_facebook_shop_n
    }
  }

  fun getIconChannel(): Int? {
    return when (accountType) {
      AccountType.facebookpage.name -> R.drawable.ic_facebook_page_n
      AccountType.facebookshop.name -> R.drawable.ic_facebook_shop_n
      AccountType.twitter.name -> R.drawable.ic_twitter_n
      AccountType.googlemybusiness.name -> R.drawable.ic_google_business_n
      else -> null
    }
  }

  fun getCardColor(): Int? {
    return when (accountType) {
      AccountType.facebookpage.name -> R.color.accent_30
      AccountType.facebookshop.name -> R.color.accent_30
      AccountType.twitter.name -> R.color.accent_30
      AccountType.googlemybusiness.name -> R.color.accent_30
      else -> R.color.accent_30
    }
  }

  enum class AccountType {
    facebookpage, facebookshop, twitter, googlemybusiness;
  }
}


fun ChannelsType.getChannelStatusList(): ArrayList<ChannelStatusData> {
  val list = ArrayList<ChannelStatusData>()
  if (this.facebookpage != null) {

  } else list.add(ChannelStatusData(accountType = ChannelStatusData.AccountType.facebookpage.name))

  if (this.facebookshop != null) {

  }

  if (this.twitter != null) {

  } else list.add(ChannelStatusData(accountType = ChannelStatusData.AccountType.facebookpage.name))

  if (this.googlemybusiness != null) {

  }
  return list
}