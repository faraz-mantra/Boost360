package com.onboarding.nowfloats.model.channel

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import com.onboarding.nowfloats.BaseBoardingApplication.Companion.instance
import com.onboarding.nowfloats.constant.ChannelPriority
import com.onboarding.nowfloats.constant.ChannelType
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.model.ProcessApiSyncModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

data class ChannelModel(
    var id: String? = null,
    var image: String? = null,
    var priority: String? = null,
    var type: String? = null,
    var link: String? = null,
    var status: String? = ProcessApiSyncModel.SyncStatus.PROCESSING.name
) : AppBaseRecyclerViewItem, Parcelable {

    var recyclerViewType = RecyclerViewItemType.CHANNEL_ITEM.getLayout()
    var isSelected = false

    override fun getViewType(): Int {
        return recyclerViewType
    }

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(image)
        writeString(priority)
        writeString(type)
        writeString(link)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ChannelModel> = object : Parcelable.Creator<ChannelModel> {
            override fun createFromParcel(source: Parcel): ChannelModel = ChannelModel(source)
            override fun newArray(size: Int): Array<ChannelModel?> = arrayOfNulls(size)
        }
    }

}

fun ChannelModel.getPriority(): ChannelPriority? {
    return ChannelPriority.values().firstOrNull { it.name.toLowerCase() == priority?.toLowerCase() }
}

fun ChannelModel.getType(): ChannelType? {
    return ChannelType.values().firstOrNull { it.name.toLowerCase() == type?.toLowerCase() }
}

fun ChannelModel.isGoogleChannel(): Boolean {
    return getType() == ChannelType.GOOGLE_MAPS || getType() == ChannelType.GOOGLE_SEARCH
            || getType() == ChannelType.GOOGLE_BUSINESS
}

fun ChannelModel.isFacebookChannel(): Boolean {
    return getType() == ChannelType.FB_PAGE || getType() == ChannelType.FB_SHOP
}

fun ChannelModel.isWhatsAppChannel(): Boolean {
    return getType() == ChannelType.WHATSAPP_BUSINESS || getType() == ChannelType.WHATSAPP_BUSINESS
}

fun ChannelModel.isTwitterChannel(): Boolean {
    return getType() == ChannelType.TWITTER_PROFILE
}

fun ChannelModel.getName(): String {
    return when (getType()) {
        ChannelType.GOOGLE_SEARCH -> instance.resources.getString(R.string.google_search)
        ChannelType.FB_PAGE -> instance.resources.getString(R.string.fb_page)
        ChannelType.GOOGLE_MAPS -> instance.resources.getString(R.string.google_maps)
        ChannelType.FB_SHOP -> instance.resources.getString(R.string.fb_shop)
        ChannelType.WHATSAPP_BUSINESS -> instance.resources.getString(R.string.whatsapp_business)
        ChannelType.TWITTER_PROFILE -> instance.resources.getString(R.string.twitter_profile)
        ChannelType.GOOGLE_BUSINESS -> instance.resources.getString(R.string.google_business)
        null -> ""
    }
}

fun ChannelModel.getDrawable(context: Context?): Drawable? {
    if (context == null) return null
    return when (getType()) {
        ChannelType.GOOGLE_SEARCH -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_google_n, context.theme)
        ChannelType.FB_PAGE -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_facebook_page_n, context.theme)
        ChannelType.GOOGLE_MAPS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_google_maps_n, context.theme)
        ChannelType.FB_SHOP -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_facebook_shop_n, context.theme)
        ChannelType.WHATSAPP_BUSINESS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_whatsapp_business_n, context.theme)
        ChannelType.TWITTER_PROFILE -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_twitter_n, context.theme)
        ChannelType.GOOGLE_BUSINESS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_google_business_n, context.theme)
        else -> null
    }
}

fun Iterable<ChannelModel>.haveFacebookChannels(): Boolean {
    return filterTo(ArrayList(), { it.isFacebookChannel() }).isNotEmpty()
}

fun Iterable<ChannelModel>.haveWhatsAppChannels(): Boolean {
    return filterTo(ArrayList(), { it.isWhatsAppChannel() }).isNotEmpty()
}

fun Iterable<ChannelModel>.haveTwitterChannels(): Boolean {
    return filterTo(ArrayList(), { it.isTwitterChannel() }).isNotEmpty()
}

fun Iterable<ChannelModel>.haveGoogleChannels(): Boolean {
    return filterTo(ArrayList(), { it.isGoogleChannel() }).isNotEmpty()
}