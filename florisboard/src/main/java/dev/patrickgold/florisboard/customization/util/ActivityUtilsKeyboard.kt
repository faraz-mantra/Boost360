package dev.patrickgold.florisboard.customization.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.appservice.ui.catalog.CatalogServiceContainerActivity
import com.appservice.ui.catalog.setFragmentType
import com.appservice.ui.staffs.ui.StaffFragmentContainerActivity
import com.appservice.ui.updatesBusiness.UpdateBusinessContainerActivity
import com.dashboard.controller.DashboardFragmentContainerActivity
import com.dashboard.controller.setFragmentType
import com.dashboard.utils.getAptType
import com.dashboard.utils.getBundleData
import com.dashboard.utils.getSessionOrder
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.*
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.setFragmentType
import com.onboarding.nowfloats.utils.WebEngageController
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.util.MethodUtils.isPackageInstalled

fun startOrderAptConsultListN(mContext: Context, isOrder: Boolean = false, isConsult: Boolean = false) {
  try {
    if (isInstall(mContext)) return
    val session = UserSessionManager(mContext)
    val txt = if (isOrder) ORDER_PAGE_CLICK else if (isConsult) CONSULTATION_PAGE_CLICK else APPOINTMENT_PAGE_CLICK
    com.dashboard.utils.WebEngageController.trackEvent(txt, CLICK, TO_BE_ADDED)
    val bundle = getSessionOrder(session)
    val fragmentType = when {
      isOrder -> com.inventoryorder.constant.FragmentType.ALL_ORDER_VIEW
      (getAptType(session.fP_AppExperienceCode) == "SPA_SAL_SVC") -> com.inventoryorder.constant.FragmentType.ALL_APPOINTMENT_SPA_VIEW
      isConsult -> com.inventoryorder.constant.FragmentType.ALL_VIDEO_CONSULT_VIEW
      else -> com.inventoryorder.constant.FragmentType.ALL_APPOINTMENT_VIEW
    }
    val intent = Intent(mContext, FragmentContainerOrderActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(fragmentType)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(intent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startBookAppointmentConsultN(mContext: Context, isConsult: Boolean = true) {
  try {
    if (isInstall(mContext)) return
    val session = UserSessionManager(mContext)
    val txt = if (isConsult) CONSULTATION_CREATE_PAGE else APPOINTMENT_CREATE_PAGE
    WebEngageController.trackEvent(txt, CLICK, TO_BE_ADDED)
    val bundle = getSessionOrder(session)
    val fragmentType = when {
      (getAptType(session.fP_AppExperienceCode) == "SPA_SAL_SVC") -> com.inventoryorder.constant.FragmentType.CREATE_SPA_APPOINTMENT
      else -> {
        bundle.putBoolean(IntentConstant.IS_VIDEO.name, isConsult)
        com.inventoryorder.constant.FragmentType.CREATE_APPOINTMENT_VIEW
      }
    }
    val intent = Intent(mContext, FragmentContainerOrderActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(fragmentType)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(intent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startOrderCreateN(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    val session = UserSessionManager(mContext)
    if (getProductType(session.fP_AppExperienceCode).equals("PRODUCTS",true)) {
      val bundle = getSessionOrder(session)
      val intent = Intent(mContext, FragmentContainerOrderActivity::class.java)
      intent.putExtras(bundle)
      intent.setFragmentType(com.inventoryorder.constant.FragmentType.CREATE_NEW_ORDER)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      mContext.startActivity(intent)
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun startTestimonialN(mContext: Context, isAdd: Boolean = false) {
  try {
    if (isInstall(mContext)) return
    val text = if (isAdd) ADD_TESTIMONIAL_PAGE else TESTIMONIAL_PAGE
    WebEngageController.trackEvent(text, CLICK, TO_BE_ADDED)
    val webIntent = Intent(mContext, Class.forName("com.nowfloats.AccrossVerticals.Testimonials.TestimonialsActivity"))
    webIntent.putExtra("IS_ADD", isAdd)
    webIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(webIntent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startBusinessEnquiryN(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    WebEngageController.trackEvent(BUSINESS_ENQUIRY_PAGE_CLICK, CLICK, TO_BE_ADDED)
    val queries = Intent(mContext, Class.forName("com.nowfloats.Business_Enquiries.BusinessEnquiryActivity"))
    queries.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(queries)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun startVmnCallCardN(mContext: Context) {
  try {
    WebEngageController.trackEvent(TRACK_CALL_PAGE_CLICK, CLICK, TO_BE_ADDED)
    val i = Intent(mContext, Class.forName("com.appservice.ui.calltracking.VmnCallCardsActivityV2"))
    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(i)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun startListServiceProductN(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    val session = UserSessionManager(mContext)
    val type = if (getProductType(session.fP_AppExperienceCode) == "SERVICES") {
      WebEngageController.trackEvent(SERVICE_INVENTORY, CLICK, TO_BE_ADDED)
      com.appservice.constant.FragmentType.SERVICE_LISTING
    } else {
      WebEngageController.trackEvent(PRODUCT_INVENTORY, CLICK, TO_BE_ADDED)
      com.appservice.constant.FragmentType.FRAGMENT_PRODUCT_LISTING
    }
    val intent = Intent(mContext, CatalogServiceContainerActivity::class.java)
    intent.putExtras(getBundleData(session))
    intent.setFragmentType(type)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(intent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startUpdateLatestStoryN(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    WebEngageController.trackEvent(UPDATE_LATEST_STORY_PAGE_CLICK, CLICK, TO_BE_ADDED)
    val intent = Intent(mContext, UpdateBusinessContainerActivity::class.java)
    intent.setFragmentType(com.appservice.constant.FragmentType.UPDATE_BUSINESS_FRAGMENT)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(intent)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun startListStaffN(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    val session = UserSessionManager(mContext)
    WebEngageController.trackEvent(LIST_STAFF_DASHBOARD, CLICK, TO_BE_ADDED)
    val intent = Intent(mContext, StaffFragmentContainerActivity::class.java)
    intent.setFragmentType(com.appservice.constant.FragmentType.STAFF_PROFILE_LISTING_FRAGMENT)
    intent.putExtras(getBundleData(session))
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(intent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startAllImageN(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    WebEngageController.trackEvent(IMAGE_MENU_PAGE, CLICK, TO_BE_ADDED)
    val webIntent = Intent(mContext, Class.forName("com.nowfloats.Image_Gallery.ImageGalleryActivity"))
    webIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(webIntent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
    SentryController.captureException(e)
  }
}

fun startCustomPageN(mContext: Context, isAdd: Boolean = false) {
  try {
    if (isInstall(mContext)) return
    val text = if (isAdd) ADD_CUSTOM_PAGE else CUSTOM_PAGE
    WebEngageController.trackEvent(text, CLICK, TO_BE_ADDED)
    val webIntent = Intent(mContext, Class.forName("com.nowfloats.CustomPage.CustomPageActivity"))
    webIntent.putExtra("IS_ADD", isAdd)
    webIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(webIntent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startBusinessHoursN(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    WebEngageController.trackEvent(BUSINESS_HOURS_PAGE, CLICK, TO_BE_ADDED)
    val webIntent = Intent(mContext, Class.forName("com.nowfloats.BusinessProfile.UI.UI.BusinessHoursActivity"))
    webIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(webIntent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startBusinessAddressN(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    WebEngageController.trackEvent(BUSINESS_ADDRESS_PAGE, CLICK, TO_BE_ADDED)
    val webIntent = Intent(mContext, Class.forName("com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity"))
    webIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(webIntent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startBusinessContactInfoN(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    com.dashboard.utils.WebEngageController.trackEvent(CONTACT_INFORMATION_PAGE, CLICK, TO_BE_ADDED)
    val webIntent = Intent(mContext, Class.forName("com.nowfloats.BusinessProfile.UI.UI.ContactInformationActivity"))
    webIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(webIntent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startUserProfileDetailN(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    WebEngageController.trackEvent(USER_MERCHANT_PROFILE_PAGE, CLICK, TO_BE_ADDED)
    val intent = Intent(mContext, DashboardFragmentContainerActivity::class.java)
    intent.setFragmentType(com.dashboard.constant.FragmentType.FRAGMENT_USER_PROFILE)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(intent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startBusinessProfileDetailEditN(mContext: Context) {
  try {
    WebEngageController.trackEvent(BUSINESS_PROFILE_PAGE, CLICK, TO_BE_ADDED)
    val intent = Intent(mContext, DashboardFragmentContainerActivity::class.java)
    intent.setFragmentType(com.dashboard.constant.FragmentType.FRAGMENT_BUSINESS_PROFILE)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(intent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun startBoostActivity(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    val packageManager = mContext.packageManager
    if (isInstall(mContext)) return
    val intent = packageManager.getLaunchIntentForPackage(mContext.packageName)
    mContext.startActivity(intent)
  } catch (e: Exception) {
    Toast.makeText(mContext, mContext.getString(R.string.unable_open_boost_app), Toast.LENGTH_SHORT).show()
  }
}

fun startKeyboardActivity(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    val intent = Intent(mContext, Class.forName("com.nowfloats.helper.AppFragmentContainerActivity"))
    intent.putExtra("FRAGMENT_TYPE", "ACCOUNT_KEYBOARD")
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(intent)
  } catch (e: Exception) {
    e.printStackTrace()
    startBoostActivity(mContext)
  }
}

fun startStaffActivity(mContext: Context) {
  try {
    if (isInstall(mContext)) return
    val intent = Intent(mContext, Class.forName("com.appservice.ui.staffs.ui.StaffFragmentContainerActivity"))
    intent.putExtra("FRAGMENT_TYPE", "STAFF_PROFILE_LISTING_FRAGMENT")
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mContext.startActivity(intent)
  } catch (e: Exception) {
    e.printStackTrace()
    startBoostActivity(mContext)
  }
}


private fun isInstall(mContext: Context): Boolean {
  val packageManager = mContext.packageManager
  if (!isPackageInstalled(mContext.packageName, packageManager)) {
    Toast.makeText(mContext, mContext.getString(R.string.app_not_install), Toast.LENGTH_SHORT).show()
    return true
  }
  return false
}