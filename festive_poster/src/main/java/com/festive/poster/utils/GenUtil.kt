package com.festive.poster.utils

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.constant.Constants
import com.festive.poster.models.BrowseAllTemplate
import com.festive.poster.models.PosterModel
import com.festive.poster.models.TemplateUi
import com.festive.poster.models.TodaysPickCategory
import com.festive.poster.models.response.TemplateSaveActionBody
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.reset.repo.NowFloatsRepository
import com.festive.poster.ui.TemplateDiffUtil
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.festive.poster.ui.promoUpdates.PromoUpdatesActivity
import com.festive.poster.ui.promoUpdates.bottomSheet.SubscribePlanBottomSheet
import com.framework.BaseApplication
import com.framework.base.BaseActivity
import com.framework.constants.IntentConstants
import com.framework.constants.PackageNames
import com.framework.constants.UPDATE_PIC_FILE_NAME
import com.framework.pref.UserSessionManager
import com.framework.utils.getResponse
import com.framework.utils.runOnUi
import com.framework.utils.saveAsImageToAppFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


fun isPromoWidgetActive(): Boolean{
    val session = UserSessionManager(BaseApplication.instance)
    return /*session.getStoreWidgets()?.contains(Constants.PROMO_WIDGET_KEY)==true*/ true
}

fun posterWhatsappShareClicked(childItem:TemplateUi,activity: BaseActivity<*,*>){
    if (isPromoWidgetActive()){
        saveTemplateAction(TemplateSaveActionBody.ActionType.SHARE,childItem)
        SvgUtils.shareUncompressedSvg(childItem.primarySvgUrl,
            PackageNames.WHATSAPP)
    }else{
        SubscribePlanBottomSheet.newInstance(object : SubscribePlanBottomSheet.Callbacks{
            override fun onBuyClick() {
                MarketPlaceUtils.launchCartActivity(activity,
                    PromoUpdatesActivity::class.java.name,null,null,childItem.tags,
                    null,childItem)

            }
        }).show(activity.supportFragmentManager, SubscribePlanBottomSheet::class.java.name)
    }
}

fun posterPostClicked(childItem:TemplateUi,activity: AppBaseActivity<*, *>){
    activity.showProgress()
    activity.lifecycleScope.launch {
        withContext(Dispatchers.Default) {
            val file = SvgUtils.svgToBitmap(childItem.primarySvgUrl)
                ?.saveAsImageToAppFolder(
                    activity.getExternalFilesDir(null)?.path +
                            File.separator + UPDATE_PIC_FILE_NAME
                )
            runOnUi {
                activity.hideProgress()
            }
            if (file?.exists() == true) {
                saveTemplateAction(TemplateSaveActionBody.ActionType.VIEW_DETAILS,childItem)
                PostPreviewSocialActivity.launchActivity(
                    activity,
                    childItem.primarySvgUrl,
                    file.path,
                    childItem.tags,
                    IntentConstants.UpdateType.UPDATE_PROMO_POST.name,
                    childItem

                )
            }

        }
    }

}

fun saveTemplateAction(action: TemplateSaveActionBody.ActionType, posterModel: TemplateUi?){
    if (posterModel==null) return
    NowFloatsRepository.saveTemplateAction(action,posterModel.isFavourite,posterModel.id).getResponse {  }
}


fun AppBaseRecyclerViewAdapter<BrowseAllTemplate>.setUpUsingDiffUtil(newList: List<BrowseAllTemplate>){
    val templateDiffUtil = TemplateDiffUtil(
        this.list,
        newList
    )

    val diffResult = DiffUtil.calculateDiff(templateDiffUtil)

    this.list.clear()
    this.list.addAll(newList)
    diffResult.dispatchUpdatesTo(this)
}

