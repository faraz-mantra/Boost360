package com.festive.poster.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.festive.poster.R
import com.festive.poster.models.PosterModel
import com.festive.poster.models.TemplateUi
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.framework.BaseApplication
import com.framework.analytics.SentryController
import com.framework.constants.IntentConstants.IK_CAPTION_KEY
import com.framework.constants.IntentConstants.IK_POSTER
import com.framework.constants.IntentConstants.IK_TAGS
import com.framework.constants.IntentConstants.IK_TEMPLATE
import com.framework.constants.IntentConstants.IK_UPDATE_TYPE
import com.framework.constants.IntentConstants.MARKET_PLACE_ORIGIN_ACTIVITY
import com.framework.constants.IntentConstants.MARKET_PLACE_ORIGIN_NAV_DATA
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.ADDON_MARKETPLACE_PAGE_CLICK
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.TO_BE_ADDED
import com.google.gson.Gson
import java.util.ArrayList

object MarketPlaceUtils {

    fun initiateAddonMarketplace(
        session: UserSessionManager,
        isOpenCardFragment: Boolean,
        screenType: String,
        buyItemKey: String?,
        isLoadingShow: Boolean = true,
        context: Context
    ) {
        try {
           // if (isLoadingShow) delayProgressShow()
            WebEngageController.trackEvent(ADDON_MARKETPLACE_PAGE_CLICK, CLICK, TO_BE_ADDED)
            val intent = Intent(context, Class.forName("com.boost.upgrades.UpgradeActivity"))
            intent.putExtra("expCode", session.fP_AppExperienceCode)
            intent.putExtra("fpName", session.fPName)
            intent.putExtra("fpid", session.fPID)
            intent.putExtra("fpTag", session.fpTag)
            intent.putExtra("isOpenCardFragment", isOpenCardFragment)
            intent.putExtra("screenType", screenType)
            intent.putExtra("accountType", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))
            intent.putExtra("boost_widget_key","TESTIMONIALS")
            intent.putExtra("feature_code","TESTIMONIALS")
            intent.putExtra("isFestivePoster",true)

            intent.putStringArrayListExtra(
                "userPurchsedWidgets",
                session.getStoreWidgets() as ArrayList<String>
            )
            if (session.userProfileEmail != null) {
                intent.putExtra("email", session.userProfileEmail)
            } else {
                intent.putExtra("email", context.getString(R.string.ria_customer_mail))
            }
            if (session.userPrimaryMobile != null) {
                intent.putExtra("mobileNo", session.userPrimaryMobile)
            } else {
                intent.putExtra("mobileNo", context.getString(R.string.ria_customer_mail))
            }
            if (buyItemKey != null && buyItemKey.isNotEmpty()) intent.putExtra("buyItemKey", buyItemKey)
            intent.putExtra("profileUrl", session.fPLogo)
            context.startActivity(intent)
           // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } catch (e: Exception) {
            SentryController.captureException(e)

            e.printStackTrace()
        }
    }

    fun launchCartActivity(activity:Activity,originActivityName:String,
                           posterImgPath:String?,caption:String?,updateType:String?,
                            template:TemplateUi?){
        val session = UserSessionManager(BaseApplication.instance)
        val intent = Intent(
            activity,
            Class.forName("com.boost.cart.CartActivity")
        )
        intent.putExtra("fpid", session.fPID)
        intent.putExtra("expCode", session.fP_AppExperienceCode)
        intent.putExtra("isDeepLink", false)
        intent.putExtra(MARKET_PLACE_ORIGIN_NAV_DATA, Bundle().apply {
            putString(MARKET_PLACE_ORIGIN_ACTIVITY,originActivityName)
            putString(IK_POSTER,posterImgPath)
            putString(IK_CAPTION_KEY,caption)
            putString(IK_UPDATE_TYPE,updateType)
            putParcelable(IK_TEMPLATE,template)
        })
        intent.putStringArrayListExtra(
            "userPurchsedWidgets",
            session.getStoreWidgets() as ArrayList<String>?
        )
        if (session.userProfileEmail != null) {
            intent.putExtra("email", session.userProfileEmail)
        } else {
            intent.putExtra("email", BaseApplication.instance.getString(R.string.ria_customer_mail))
        }
        if (session.userPrimaryMobile != null) {
            intent.putExtra("mobileNo", session.userPrimaryMobile)
        } else {
            intent.putExtra("mobileNo", BaseApplication.instance.getString(R.string.ria_customer_mail))
        }
        intent.putExtra("profileUrl",session.fPLogo)
        activity.startActivity(intent)
    }
}