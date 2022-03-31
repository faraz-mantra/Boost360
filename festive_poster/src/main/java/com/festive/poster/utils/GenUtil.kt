package com.festive.poster.utils

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.festive.poster.constant.Constants
import com.festive.poster.models.PosterModel
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.festive.poster.ui.promoUpdates.PromoUpdatesActivity
import com.festive.poster.ui.promoUpdates.bottomSheet.SubscribePlanBottomSheet
import com.framework.BaseApplication
import com.framework.base.BaseActivity
import com.framework.constants.IntentConstants
import com.framework.constants.PackageNames
import com.framework.constants.UPDATE_PIC_FILE_NAME
import com.framework.pref.UserSessionManager
import com.framework.utils.saveAsImageToAppFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


fun isPromoWidgetActive(): Boolean{
    val session = UserSessionManager(BaseApplication.instance)
    return /*session.getStoreWidgets()?.contains(Constants.PROMO_WIDGET_KEY)==true*/ true
}

fun posterWhatsappShareClicked(childItem:PosterModel,activity: BaseActivity<*,*>){
    val variant = childItem.variants?.firstOrNull()
    if (isPromoWidgetActive()){

        SvgUtils.shareUncompressedSvg(variant?.svgUrl,childItem,
            BaseApplication.instance, PackageNames.WHATSAPP)
    }else{
        SubscribePlanBottomSheet.newInstance(object : SubscribePlanBottomSheet.Callbacks{
            override fun onBuyClick() {
                MarketPlaceUtils.launchCartActivity(activity,
                    PromoUpdatesActivity::class.java.name,null,null,childItem.tags,
                    null)

            }
        }).show(activity.supportFragmentManager, SubscribePlanBottomSheet::class.java.name)
    }
}

fun posterPostClicked(childItem:PosterModel,activity: BaseActivity<*,*>){
    activity.lifecycleScope.launch {
        withContext(Dispatchers.Default) {
            val file = SvgUtils.svgToBitmap(childItem as PosterModel)
                ?.saveAsImageToAppFolder(
                    activity?.getExternalFilesDir(null)?.path +
                            File.separator + UPDATE_PIC_FILE_NAME
                )
            if (file?.exists() == true) {
                PostPreviewSocialActivity.launchActivity(
                    activity,
                    childItem.greeting_message,
                    file.path,
                    childItem.tags,
                    IntentConstants.UpdateType.UPDATE_PROMO_POST.name

                )
            }

        }
    }

}

