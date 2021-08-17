package com.onboarding.nowfloats.model.channel

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import com.onboarding.nowfloats.BaseBoardingApplication.Companion.instance
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.model.ProcessApiSyncModel
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.ChannelActionData
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem
import java.util.*
import kotlin.collections.ArrayList

data class ChannelModel(
    var moreDesc: String? = null,
    var priority: String? = null,
    var type: String? = null,
    var isSelected: Boolean? = false,
    var status: String? = ProcessApiSyncModel.SyncStatus.PROCESSING.name,
) : AppBaseRecyclerViewItem, Parcelable {

  var websiteUrl: String? = ""
  var isSelectedClick: Boolean = false

  var channelAccessToken: ChannelAccessToken? = null
  var channelActionData: ChannelActionData? = null

  var recyclerViewType = RecyclerViewItemType.CHANNEL_ITEM.getLayout()

  constructor(parcel: Parcel) : this(
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
      parcel.readString()) {
    recyclerViewType = parcel.readInt()
  }

  override fun getViewType(): Int {
    return recyclerViewType
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(moreDesc)
    parcel.writeString(priority)
    parcel.writeString(type)
    parcel.writeValue(isSelected)
    parcel.writeString(status)
    parcel.writeInt(recyclerViewType)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ChannelModel> {
    override fun createFromParcel(parcel: Parcel): ChannelModel {
      return ChannelModel(parcel)
    }

    override fun newArray(size: Int): Array<ChannelModel?> {
      return arrayOfNulls(size)
    }
  }

  fun dummyData(): ArrayList<ChannelModel> {
    val list = ArrayList<ChannelModel>()
    list.add(ChannelModel("", type = ChannelType.G_SEARCH.name, isSelected = true))
    list.add(ChannelModel("", type = ChannelType.G_MAPS.name, isSelected = true))
    list.add(ChannelModel("", type = ChannelType.FB_PAGE.name, isSelected = true))
    list.add(ChannelModel("", type = ChannelType.FB_SHOP.name, isSelected = true))
    list.add(ChannelModel("", type = ChannelType.WAB.name, isSelected = true))
    list.add(ChannelModel("", type = ChannelType.T_FEED.name, isSelected = false))
    list.add(ChannelModel("", type = ChannelType.G_BUSINESS.name, isSelected = false))
    return list
  }
}

fun ChannelModel.getPriority(): ChannelPriority? {
  return ChannelPriority.values().firstOrNull { it.name.equals(priority, ignoreCase = true) }
}

fun ChannelModel.getType(): ChannelType? {
  return ChannelType.values().firstOrNull { it.name.equals(type, ignoreCase = true) }
}

fun ChannelModel.isGoogleChannel(): Boolean {
  return getType() == ChannelType.G_MAPS || getType() == ChannelType.G_SEARCH // || getType() == ChannelType.G_BUSINESS
}

fun ChannelModel.isGoogleSearch(): Boolean {
  return getType() == ChannelType.G_SEARCH
}

fun ChannelModel.isGoogleBusinessChannel(): Boolean {
  return getType() == ChannelType.G_BUSINESS
}

fun ChannelModel.isFacebookShop(): Boolean {
  return getType() == ChannelType.FB_SHOP
}

fun ChannelModel.isFacebookPage(): Boolean {
  return getType() == ChannelType.FB_PAGE
}


fun ChannelModel.isWhatsAppChannel(): Boolean {
  return getType() == ChannelType.WAB
}

fun ChannelModel.isTwitterChannel(): Boolean {
  return getType() == ChannelType.T_FEED
}

fun ChannelModel.getName(): String {
  return when (getType()) {
    ChannelType.G_SEARCH -> instance.resources.getString(R.string.website)
    ChannelType.FB_PAGE -> instance.resources.getString(R.string.fb_page)
    ChannelType.G_MAPS -> instance.resources.getString(R.string.google_maps)
    ChannelType.FB_SHOP -> instance.resources.getString(R.string.fb_shop)
    ChannelType.WAB -> instance.resources.getString(R.string.whatsapp_business)
    ChannelType.T_FEED -> instance.resources.getString(R.string.twitter_profile)
    ChannelType.G_BUSINESS -> instance.resources.getString(R.string.google_business)
    null -> ""
  }
}

fun ChannelModel.getName1(): String {
  return when (getType()) {
    ChannelType.G_SEARCH -> instance.resources.getString(R.string.website1)
    ChannelType.FB_PAGE -> instance.resources.getString(R.string.fb_page1)
    ChannelType.G_MAPS -> instance.resources.getString(R.string.google_maps1)
    ChannelType.FB_SHOP -> instance.resources.getString(R.string.fb_shop1)
    ChannelType.WAB -> instance.resources.getString(R.string.whatsapp_business1)
    ChannelType.T_FEED -> instance.resources.getString(R.string.twitter_profile1)
    ChannelType.G_BUSINESS -> instance.resources.getString(R.string.google_business_n1)
    null -> ""
  }
}

fun ChannelModel.getNameAlternate(): String {
  return when (getType()) {
    ChannelType.G_SEARCH -> "Website"
    ChannelType.FB_PAGE -> "FP Page"
    ChannelType.G_MAPS -> "Maps"
    ChannelType.FB_SHOP -> "FP Shop"
    ChannelType.WAB -> "WA Profile"
    ChannelType.T_FEED -> "Twitter"
    ChannelType.G_BUSINESS -> "Maps"
    null -> ""
  }
}

fun ChannelModel.getAccessTokenType(): String {
  return when (getType()) {
    ChannelType.G_SEARCH -> ChannelAccessToken.AccessTokenType.googlesearch.name
    ChannelType.FB_PAGE -> ChannelAccessToken.AccessTokenType.facebookpage.name
    ChannelType.G_MAPS -> ChannelAccessToken.AccessTokenType.googlemap.name
    ChannelType.FB_SHOP -> ChannelAccessToken.AccessTokenType.facebookshop.name
    ChannelType.WAB -> ChannelType.WAB.name
    ChannelType.T_FEED -> ChannelAccessToken.AccessTokenType.twitter.name
    ChannelType.G_BUSINESS -> ChannelAccessToken.AccessTokenType.googlemybusiness.name
    null -> ""
  }
}

fun ChannelModel.getDrawable(context: Context?): Drawable? {
  if (context == null) return null
  return when (getType()) {
    ChannelType.G_SEARCH -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_website, context.theme)
    ChannelType.FB_PAGE -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_facebook_page_n, context.theme)
    ChannelType.G_MAPS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_google_maps_n, context.theme)
    ChannelType.FB_SHOP -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_facebook_shop_n, context.theme)
    ChannelType.WAB -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_whatsapp_business_n, context.theme)
    ChannelType.T_FEED -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_twitter_n, context.theme)
    ChannelType.G_BUSINESS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_google_maps_n, context.theme)
    else -> null
  }
}

fun ChannelModel.getDrawableActiveNew(context: Context?): Drawable? {
  if (context == null) return null
  return when (getType()) {
    ChannelType.G_SEARCH -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_website_active, context.theme)
    ChannelType.FB_PAGE -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_facebook_page_active, context.theme)
    ChannelType.G_MAPS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_google_maps_active, context.theme)
    ChannelType.FB_SHOP -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_facebook_shop_active, context.theme)
    ChannelType.WAB -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_whatsapp_business_active, context.theme)
    ChannelType.T_FEED -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_twitter_active, context.theme)
    ChannelType.G_BUSINESS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_google_maps_active, context.theme)
    else -> null
  }
}

fun ChannelModel.getDrawableInActiveNew(context: Context?): Drawable? {
  if (context == null) return null
  return when (getType()) {
    ChannelType.G_SEARCH -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_website_inactive, context.theme)
    ChannelType.FB_PAGE -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_facebook_page_inactive, context.theme)
    ChannelType.G_MAPS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_google_maps_inactive, context.theme)
    ChannelType.FB_SHOP -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_facebook_shop_inactive, context.theme)
    ChannelType.WAB -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_whatsapp_business_inactive, context.theme)
    ChannelType.T_FEED -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_twitter_inactive, context.theme)
    ChannelType.G_BUSINESS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_google_maps_inactive, context.theme)
    else -> null
  }
}


fun Iterable<ChannelModel>.haveFacebookShop(): Boolean {
  return filterTo(ArrayList(), { it.isFacebookShop() }).isNotEmpty()
}

fun Iterable<ChannelModel>.haveFacebookPage(): Boolean {
  return filterTo(ArrayList(), { it.isFacebookPage() }).isNotEmpty()
}

fun Iterable<ChannelModel>.haveWhatsAppChannels(): Boolean {
  return filterTo(ArrayList(), { it.isWhatsAppChannel() }).isNotEmpty()
}

fun Iterable<ChannelModel>.isFbPageOrShop(channelType: ChannelType?): ChannelModel? {
  return this.singleOrNull { it.getType() == channelType }
}

fun Iterable<ChannelModel>.haveTwitterChannels(): Boolean {
  return filterTo(ArrayList(), { it.isTwitterChannel() }).isNotEmpty()
}

fun Iterable<ChannelModel>.haveGoogleBusinessChannel(): Boolean {
  return filterTo(ArrayList(), { it.isGoogleBusinessChannel() }).isNotEmpty()
}

fun Iterable<ChannelModel>.haveGoogleChannels(): Boolean {
  return filterTo(ArrayList(), { it.isGoogleChannel() }).isNotEmpty()
}