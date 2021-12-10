package com.festive.poster.models.promoModele

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import com.onboarding.nowfloats.model.channel.ChannelType
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import java.io.Serializable

class SocialPlatformModel(
    val socialTitle: String? = null,
    var socialSubTitleData: String? = null,
    var isEnabled: Boolean? = false,
    var isConnected: Boolean? = false
) : Serializable, AppBaseRecyclerViewItem {

    var icon:Drawable?=null

    fun generateTitle(channelAccessToken: ChannelAccessToken){


    }

    fun generateImageResource(type:ChannelType?,context: Context)
    {
        icon= when (type) {
            ChannelType.G_SEARCH -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_promo_my_website,
                context.theme
            )
            ChannelType.FB_PAGE -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_promo_flag_orange,
                context.theme
            )
            ChannelType.G_MAPS -> ResourcesCompat.getDrawable(
                context.resources,
                com.onboarding.nowfloats.R.drawable.ic_google_maps_n,
                context.theme
            )
            ChannelType.FB_SHOP -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_promo_flag_orange,
                context.theme
            )
            ChannelType.WAB -> ResourcesCompat.getDrawable(
                context.resources,
                com.onboarding.nowfloats.R.drawable.ic_whatsapp_business_n,
                context.theme
            )
            ChannelType.T_FEED -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_promo_twitter,
                context.theme
            )
            ChannelType.G_BUSINESS -> ResourcesCompat.getDrawable(
                context.resources,
                com.onboarding.nowfloats.R.drawable.ic_google_maps_n,
                context.theme
            )
            else -> null
        }

    }

    fun getData(context: Context): ArrayList<SocialPlatformModel> {
        return arrayListOf(
            /*SocialPlatformModel(
                socialTitle = "Jerin Dany",
                socialSubTitleData = "http//facebook.com/MyShop",
            //    socialImageResource = R.drawable.ic_promo_flag_orange,
                isEnabled = false,
                isConnected = true
            ),
            SocialPlatformModel(
                socialTitle = "Instagram",
                socialSubTitleData = "@jerindany23",
               // socialImageResource = R.drawable.ic_promo_instagram,
                isEnabled = false,
                isConnected = false
            ),
            SocialPlatformModel(
                socialTitle = "My Shop",
                socialSubTitleData = "http//facebook.com/MyShop",
             //   socialImageResource = R.drawable.ic_promo_myshop,
                isEnabled= true,
                isConnected = true
            ),
            SocialPlatformModel(
                socialTitle = "Jerin’s Shop",
                socialSubTitleData = "@jerindany23",
               // socialImageResource = R.drawable.ic_promo_twitter,
                isEnabled= false,
                isConnected = false
            ),
            SocialPlatformModel(
                socialTitle = "My Website",
                socialSubTitleData = "http//www.myshop.com",
              //  socialImageResource = R.drawable.ic_promo_my_website,
                isEnabled = false,
                isConnected = true
            ),
            SocialPlatformModel(
                socialTitle = "Emailers",
                socialSubTitleData = "jerin@thence.co",
             //   socialImageResource = R.drawable.ic_promo_emailers,
                isEnabled = false,
                isConnected = true
            )*/
        )
    }

    override fun getViewType(): Int {
        return RecyclerViewItemType.SOCIAL_PLATFORM_POST_OPTIONS_LIST.getLayout()
    }
}