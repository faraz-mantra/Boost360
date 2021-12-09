package com.festive.poster.models.promoModele

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class SocialPlatformModel(
    val socialTitle: String? = null,
    var socialSubTitleData: String? = null,
    val socialImageResource: Drawable? = null,
    var isEnabled: Boolean? = false,
    var isConnected: Boolean? = false
) : Serializable, AppBaseRecyclerViewItem {

    fun getData(context: Context): ArrayList<SocialPlatformModel> {
        return arrayListOf(
            SocialPlatformModel(
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
            )
        )
    }

    override fun getViewType(): Int {
        return RecyclerViewItemType.SOCIAL_PLATFORM_POST_OPTIONS_LIST.getLayout()
    }
}