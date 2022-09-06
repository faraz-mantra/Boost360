package com.festive.poster.utils

import android.app.Activity
import android.content.Intent
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
import com.festive.poster.ui.promoUpdates.bottomSheet.PromoteBrandedUpdateTemplatesBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.SubscribePlanBottomSheet
import com.framework.BaseApplication
import com.framework.base.BaseActivity
import com.framework.base.setFragmentType
import com.framework.constants.IntentConstants
import com.framework.constants.PackageNames
import com.framework.constants.UPDATE_PIC_FILE_NAME
import com.framework.firebaseUtils.firestore.FirestoreManager.getDrScoreData
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
    return session.getStoreWidgets()?.contains(Constants.UPDATES_STUDIO_WIDGET_KEY)==true
}

fun posterWhatsappShareClicked(childItem:TemplateUi,activity: BaseActivity<*,*>){
    if (isPromoWidgetActive()){
        saveTemplateAction(TemplateSaveActionBody.ActionType.SHARE,childItem)
        SvgUtils.shareUncompressedSvg(childItem.primarySvgUrl,
            childItem.primaryText,
            PackageNames.WHATSAPP)
    }else{
        PromoteBrandedUpdateTemplatesBottomSheet
            .newInstance().show(activity.supportFragmentManager, PromoteBrandedUpdateTemplatesBottomSheet::class.java.name)
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
                    childItem.primaryText+"\n"+
                            convertToHashTag(
                        childItem.tags),
                    file.path,
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


fun convertToHashTag(list:List<String>?): String {
    if (list==null) return ""
  return  list.map { "#"+it }.joinToString(" ")
}

fun launchPostNewUpdate(activity:Activity){
    val intent = Intent(activity, Class.forName("com.appservice.ui.updatesBusiness.UpdateBusinessContainerActivity"))
    intent.setFragmentType("ADD_UPDATE_BUSINESS_FRAGMENT_V2")
    activity.startActivity(intent)
}

fun isBusinessLogoUpdated() : Boolean {
    return if (getDrScoreData() != null && getDrScoreData()!!.metricdetail != null) {
        getDrScoreData()!!.metricdetail!!.boolean_add_clinic_logo == true
    } else false
}
