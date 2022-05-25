package com.dashboard.model.live.channel

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import com.onboarding.nowfloats.model.channel.insights.InsightsData
import com.onboarding.nowfloats.model.channel.statusResponse.CHANNEL_STATUS_SUCCESS
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelsType


const val CHANNEL_STATUS_DATA = "CHANNEL_STATUS_DATA"

class ChannelStatusData(
  @SerializedName("account_type")
  var accountType: String? = null,
  @SerializedName("total_count")
  var totalCount: Int? = null,
  @SerializedName("is_connected")
  var isConnected: Boolean = false,
  var insightsData: InsightsData? = null,
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.CHANNEL_STATUS_ITEM_VIEW.getLayout()
  }

  fun getIconConnected(): Int {
    return when (isConnected) {
      true -> R.drawable.ic_connect_social_media
      false -> R.drawable.ic_add_social_media
    }
  }

  fun getIconChannel(): Int? {
    return when (accountType) {
      ChannelsType.AccountType.facebookpage.name -> R.drawable.ic_facebook_page_n
      ChannelsType.AccountType.facebookshop.name -> R.drawable.ic_facebook_shop_n
      ChannelsType.AccountType.twitter.name -> R.drawable.ic_twitter_n
      ChannelsType.AccountType.googlemybusiness.name -> R.drawable.ic_google_business_n
      ChannelsType.AccountType.instagram.name -> R.drawable.ic_instagram_white_circle_dashboard
      else -> null
    }
  }

  fun getNotActiveString(): Int? {
    return when (accountType) {
      ChannelsType.AccountType.facebookpage.name -> R.string.fb_page_sync_not_activated_yet
      ChannelsType.AccountType.facebookshop.name -> R.string.fb_shop_sync_not_activated_yet
      ChannelsType.AccountType.twitter.name -> R.string.fb_twitter_sync_not_activated_yet
      ChannelsType.AccountType.googlemybusiness.name -> R.string.fb_gmb_sync_not_activated_yet
      ChannelsType.AccountType.instagram.name -> R.string.ig_sync_not_activated_yet

      else -> null
    }
  }

  fun getActiveString(): Int? {
    return when (accountType) {
      ChannelsType.AccountType.facebookpage.name -> R.string.total_fp_page_followers
      ChannelsType.AccountType.facebookshop.name -> R.string.total_fp_shop_followers
      ChannelsType.AccountType.twitter.name -> R.string.total_twitter_followers
      ChannelsType.AccountType.googlemybusiness.name -> R.string.total_gmb_followers
      ChannelsType.AccountType.instagram.name -> R.string.total_ig_page_followers
      else -> null
    }
  }

  fun getCardColor(): Int {
    return when (accountType) {
      ChannelsType.AccountType.facebookpage.name -> R.color.dusky_blue_10
      ChannelsType.AccountType.facebookshop.name -> R.color.dusky_blue_10
      ChannelsType.AccountType.twitter.name -> R.color.azure_10
      ChannelsType.AccountType.googlemybusiness.name -> R.color.blue_light_10
      ChannelsType.AccountType.instagram.name -> R.color.dusky_blue_10
      else -> R.color.dusky_blue_10
    }
  }
}

fun getChannelStatus(): ArrayList<ChannelStatusData> {
  val resp = PreferencesUtils.instance.getData(CHANNEL_STATUS_DATA, "") ?: ""
  return ArrayList(convertStringToList(resp) ?: ArrayList())
}

fun saveDataChannelStatus(channelStatus: ArrayList<ChannelStatusData>?) {
  PreferencesUtils.instance.saveData(
    CHANNEL_STATUS_DATA,
    convertListObjToString(channelStatus ?: ArrayList()) ?: ""
  )
}


fun ChannelsType?.getChannelStatusList(): ArrayList<ChannelStatusData> {
  val list = ArrayList<ChannelStatusData>()
  if (this != null) {
    val isConnectedFpPage = this.facebookpage?.status?.equals(CHANNEL_STATUS_SUCCESS, true) ?: false
    list.add(
      ChannelStatusData(
        accountType = ChannelsType.AccountType.facebookpage.name,
        isConnected = isConnectedFpPage
      )
    )

    val isConnectedFpShop = this.facebookshop?.status?.equals(CHANNEL_STATUS_SUCCESS, true) ?: false
    if (isConnectedFpShop) list.add(
      ChannelStatusData(
        accountType = ChannelsType.AccountType.facebookshop.name,
        isConnected = isConnectedFpShop
      )
    )

    val isConnectedTwitter = this.twitter?.status?.equals(CHANNEL_STATUS_SUCCESS, true) ?: false
    list.add(
      ChannelStatusData(
        accountType = ChannelsType.AccountType.twitter.name,
        isConnected = isConnectedTwitter
      )
    )

    val isConnectedGmb =
      this.googlemybusiness?.status?.equals(CHANNEL_STATUS_SUCCESS, true) ?: false
    list.add(
      ChannelStatusData(
        accountType = ChannelsType.AccountType.googlemybusiness.name,
        isConnected = isConnectedGmb
      )
    )
    val isConnectedInstagram =
      this.instagram?.status?.equals(CHANNEL_STATUS_SUCCESS, true) ?: false
    list.add(
      ChannelStatusData(
        accountType = ChannelsType.AccountType.instagram.name,
        isConnected = isConnectedInstagram
      )
    )
  } else {
    list.add(
      ChannelStatusData(
        accountType = ChannelsType.AccountType.facebookpage.name,
        isConnected = false
      )
    )
    list.add(
      ChannelStatusData(
        accountType = ChannelsType.AccountType.twitter.name,
        isConnected = false
      )
    )
    list.add(
      ChannelStatusData(
        accountType = ChannelsType.AccountType.googlemybusiness.name,
        isConnected = false
      )
    )
    list.add(
      ChannelStatusData(
        accountType = ChannelsType.AccountType.instagram.name,
        isConnected = false
      )
    )
  }
  return list
}