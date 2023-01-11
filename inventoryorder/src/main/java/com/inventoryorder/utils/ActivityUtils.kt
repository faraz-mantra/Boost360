package com.inventoryorder.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.WA_KEY
import com.framework.pref.getDomainName
import com.framework.webengageconstant.*
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.ui.startFragmentOrderActivity

fun AppCompatActivity.startOrderAptConsultList(session: UserSessionManager?, isOrder: Boolean = false, isConsult: Boolean = false) {
    try {
        val txt = if (isOrder) ORDER_PAGE_CLICK else if (isConsult) CONSULTATION_PAGE_CLICK else APPOINTMENT_PAGE_CLICK
        WebEngageController.trackEvent(txt, CLICK, TO_BE_ADDED)
        val bundle = getSessionOrder(session)
        val fragmentType = when {
            isOrder -> com.inventoryorder.constant.FragmentType.ALL_ORDER_VIEW
            (getAptType(session?.fP_AppExperienceCode) == "SPA_SAL_SVC") -> com.inventoryorder.constant.FragmentType.ALL_APPOINTMENT_SPA_VIEW
            isConsult -> com.inventoryorder.constant.FragmentType.ALL_VIDEO_CONSULT_VIEW
            else -> com.inventoryorder.constant.FragmentType.ALL_APPOINTMENT_VIEW
        }
        this.startFragmentOrderActivity(type = fragmentType, bundle = bundle, isResult = true)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}

fun getSessionOrder(session: UserSessionManager?): Bundle {
    val data = PreferenceData(
        AppConstant.CLIENT_ID_ORDER, session?.userProfileId, WA_KEY, session?.fpTag, session?.userPrimaryMobile, session?.getDomainName(false), session?.fPEmail, session?.getFPDetails(
            Key_Preferences.LATITUDE), session?.getFPDetails(Key_Preferences.LONGITUDE), session?.fP_AppExperienceCode
    )
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, data)
    return bundle
}