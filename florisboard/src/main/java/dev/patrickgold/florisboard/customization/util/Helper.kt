package dev.patrickgold.florisboard.customization.util

import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.LifecycleOwner
import com.framework.pref.UserSessionManager
import java.util.*


fun getProductType(category_code: String?): String {
  return when (category_code) {
    "SVC", "DOC", "HOS", "SPA", "SAL" -> "Services"
    else -> "Products"
  }
}

fun isStaffVisible(category_code: String?):Boolean {
  return when (category_code) {
    "SPA", "SAL", "SVC" -> true
    else -> false
  }
}

fun UserSessionManager.getIconCard(): Int {
  return when (this.fP_AppExperienceCode?.toUpperCase(Locale.ROOT)) {
    "DOC" -> com.onboarding.nowfloats.R.drawable.ic_business_card_doctor_hospital_d
    "HOS" -> com.onboarding.nowfloats.R.drawable.ic_business_card_doctor_hospital_d
    "RTL" -> com.onboarding.nowfloats.R.drawable.ic_business_card_retail_d
    "EDU" -> com.onboarding.nowfloats.R.drawable.ic_business_card_education_d
    "HOT" -> com.onboarding.nowfloats.R.drawable.ic_business_card_hotel_d
    "MFG" -> com.onboarding.nowfloats.R.drawable.ic_business_card_manufacture_d
    "CAF" -> com.onboarding.nowfloats.R.drawable.ic_business_card_restaurant_d
    "SVC" -> com.onboarding.nowfloats.R.drawable.ic_business_card_services_d
    "SPA" -> com.onboarding.nowfloats.R.drawable.ic_business_card_spa_n
    "SAL" -> com.onboarding.nowfloats.R.drawable.ic_business_card_spa_n
    else -> com.onboarding.nowfloats.R.drawable.ic_business_card_spa_n
  }
}

fun Context.getLifecycleOwner(): LifecycleOwner {
  return try {
    this as LifecycleOwner
  } catch (exception: ClassCastException) {
    (this as ContextWrapper).baseContext as LifecycleOwner
  }
}