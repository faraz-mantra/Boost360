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
    var isConnected: Boolean? = false,
    var isChecked:Boolean?=false,
    var warning:String?=null,
    var channelType:SocialPreviewChannel
) : Serializable, AppBaseRecyclerViewItem {

    var icon:Drawable?=null

    fun generateTitle(channelAccessToken: ChannelAccessToken){


    }

    fun generateImageResource(context: Context)
    {
        icon= when (channelType) {
            SocialPreviewChannel.WEBSITE -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_promo_my_website,
                context.theme
            )
            SocialPreviewChannel.FACEBOOK -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_promo_flag_orange,
                context.theme
            )
            SocialPreviewChannel.GMB -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_gmb_promo_channel,
                context.theme
            )
        /*    SocialPreviewChannel.WEBSITE -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_promo_flag_orange,
                context.theme
            )*/
/*            ChannelType.WAB -> ResourcesCompat.getDrawable(
                context.resources,
                com.onboarding.nowfloats.R.drawable.ic_whatsapp_business_n,
                context.theme
            )*/
            SocialPreviewChannel.TWITTER-> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_promo_twitter,
                context.theme
            )
            SocialPreviewChannel.INSTAGRAM -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_promo_instagram,
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