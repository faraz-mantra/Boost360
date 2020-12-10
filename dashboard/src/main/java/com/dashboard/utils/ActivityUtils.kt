package com.dashboard.utils

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dashboard.R
import com.dashboard.controller.getDomainName
import com.dashboard.pref.*
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.ui.startFragmentOrderActivity
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.ui.updateChannel.startFragmentActivity

fun AppCompatActivity.startDigitalChannel(session: UserSessionManager) {
  try {
    val bundle = Bundle()
    val rootAlisasURI: String = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI) ?: ""
    session.setHeader(WA_KEY)
    bundle.putString(UserSessionManager.KEY_FP_ID, session.fPID)
    bundle.putString(Key_Preferences.GET_FP_DETAILS_TAG, session.fpTag)
    bundle.putString(Key_Preferences.GET_FP_EXPERIENCE_CODE, session.fP_AppExperienceCode)
    bundle.putBoolean(Key_Preferences.IS_UPDATE, true)
    bundle.putString(Key_Preferences.BUSINESS_NAME, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME))
    var imageUri = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI)
    if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("http").not()) {
      imageUri = BASE_IMAGE_URL + imageUri
    }
    bundle.putString(Key_Preferences.BUSINESS_IMAGE, imageUri)
    bundle.putString(Key_Preferences.BUSINESS_TYPE, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))

    val city = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY)
    val country = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY)
    bundle.putString(Key_Preferences.LOCATION, if (city.isNullOrEmpty().not() && country.isNullOrEmpty().not()) "$city, $country" else "$city$country")
    bundle.putString(Key_Preferences.WEBSITE_URL, session.getDomainName(false))
    bundle.putString(Key_Preferences.PRIMARY_NUMBER, session.userPrimaryMobile)
    bundle.putString(Key_Preferences.PRIMARY_EMAIL, session.fPEmail)
    startFragmentActivity(FragmentType.MY_DIGITAL_CHANNEL, bundle)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startVmnCallCard() {
  try {
    val i = Intent(this, Class.forName("com.nowfloats.Analytics_Screen.VmnCallCardsActivity"))
    startActivity(i)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBusinessEnquiry() {
  try {
    val queries = Intent(this, Class.forName("com.nowfloats.Business_Enquiries.BusinessEnquiryActivity"))
    startActivity(queries)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startSubscriber() {
  try {
    val subscribers = Intent(this, Class.forName("com.nowfloats.Analytics_Screen.SubscribersActivity"))
    startActivity(subscribers)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.initiateAddonMarketplace(session: UserSessionManager, isOpenCardFragment: Boolean, screenType: String, buyItemKey: String?) {
  try {
    val intent = Intent(this, Class.forName("com.boost.upgrades.UpgradeActivity"))
    intent.putExtra("expCode", session.fP_AppExperienceCode)
    intent.putExtra("fpName", session.fPName)
    intent.putExtra("fpid", session.fPID)
    intent.putExtra("fpTag", session.fpTag)
    intent.putExtra("isOpenCardFragment", isOpenCardFragment)
    intent.putExtra("screenType", screenType)
    intent.putExtra("accountType", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))
    intent.putStringArrayListExtra("userPurchsedWidgets", StoreWidgets)
    if (session.fPEmail != null) {
      intent.putExtra("email", session.fPEmail)
    } else {
      intent.putExtra("email", "ria@nowfloats.com")
    }
    if (session.fPPrimaryContactNumber != null) {
      intent.putExtra("mobileNo", session.fPPrimaryContactNumber)
    } else {
      intent.putExtra("mobileNo", "9160004303")
    }
    if (buyItemKey != null && buyItemKey.isNotEmpty()) intent.putExtra("buyItemKey", buyItemKey)
    intent.putExtra("profileUrl", session.fPLogo)
    startActivity(intent)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startSettingActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "ACCOUNT_SETTING")
}

fun AppCompatActivity.startKeyboardActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "ACCOUNT_KEYBOARD")
}

fun AppCompatActivity.startManageContentActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "MANAGE_CONTENT")
}

fun AppCompatActivity.startManageInventoryActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "MANAGE_INVENTORY")
}

fun AppCompatActivity.startHelpAndSupportActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "HELP_AND_SUPPORT")
}

fun AppCompatActivity.startAboutBoostActivity(session: UserSessionManager) {
  startAppActivity(fragmentType = "ABOUT_BOOST")
}

fun AppCompatActivity.startAppActivity(bundle: Bundle = Bundle(), fragmentType: String) {
  try {
    val intent = Intent(this, Class.forName("com.nowfloats.helper.AppFragmentContainerActivity"))
    intent.putExtras(bundle)
    intent.putExtra("FRAGMENT_TYPE", fragmentType)
    startActivity(intent)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startPostUpdate(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.NavigationDrawer.Create_Message_Activity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startAddImageGallery(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.Image_Gallery.ImageGalleryActivity"))
    webIntent.putExtra("create_image", true)
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startAddTestimonial(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.AccrossVerticals.Testimonials.TestimonialsActivity"))
    webIntent.putExtra("IS_ADD", true)
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startCreateCustomPage(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.CustomPage.CustomPageActivity"))
    webIntent.putExtra("IS_ADD", true)
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startListServiceProduct(session: UserSessionManager?) {
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.ProductGallery.ProductCatalogActivity"))
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startAddServiceProduct(session: UserSessionManager?) {
  val type: String = getProductType(session?.fP_AppExperienceCode)
  try {
    val webIntent = Intent(this, Class.forName("com.nowfloats.ProductGallery.ProductCatalogActivity"))
    webIntent.putExtra("IS_ADD", true)
    startActivity(webIntent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}

fun AppCompatActivity.startBookAppointmentConsult(session: UserSessionManager?, isConsult: Boolean = true) {
  try {
    val data = PreferenceData(clientId_ORDER, session?.userProfileId, WA_KEY, session?.fpTag, session?.userPrimaryMobile,
        session?.getDomainName(false), session?.fPEmail, session?.getFPDetails(Key_Preferences.LATITUDE),
        session?.getFPDetails(Key_Preferences.LONGITUDE), session?.fP_AppExperienceCode)
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, data)
    bundle.putBoolean(IntentConstant.IS_VIDEO.name, isConsult)
    this.startFragmentOrderActivity(com.inventoryorder.constant.FragmentType.CREATE_APPOINTMENT_VIEW, bundle, isResult = true)
  } catch (e: ClassNotFoundException) {
    e.printStackTrace()
  }
}
//fun AppCompatActivity.referFriend(session: UserSessionManager){
//  if (!TextUtils.isEmpty(session.fPEmail)) {
//    InviteReferralsApi.getInstance(applicationContext).userDetails(
//        session.userProfileName,
//        session.fPEmail,
//        session.userPrimaryMobile,
//        REFERRAL_CAMPAIGN_CODE, null, null
//    )
//    inviteReferralLogin(session)
//  } else if (!TextUtils.isEmpty(session.userProfileEmail)) {
//    InviteReferralsApi.getInstance(applicationContext).userDetails(
//        session.userProfileName,
//        session.userProfileEmail,
//        session.userPrimaryMobile,
//        REFERRAL_CAMPAIGN_CODE, null, null
//    )
//    inviteReferralLogin(session)
//  } else {
//    Toast.makeText(applicationContext, "An unexpected error occured.", Toast.LENGTH_LONG).show()
//  }
//}
//
//fun AppCompatActivity.inviteReferralLogin(session: UserSessionManager) {
//  InviteReferralsApi.getInstance(this).userDetailListener(object : UserDetailsCallback() {
//    fun userDetails(jsonObject: JSONObject) {
//      Log.d("Referral Details", jsonObject.toString())
//      try {
//        val status = jsonObject["Authentication"].toString()
//        if (status.toLowerCase() == "success") {
//          InviteReferralsApi.getInstance(this).inline_btn(REFERRAL_CAMPAIGN_CODE)
//        } else {
//          Toast.makeText(applicationContext, "Authentication failed. Please try later.", Toast.LENGTH_SHORT).show()
//        }
//      } catch (e: JSONException) {
//        Toast.makeText(applicationContext, "Authentication failed. Please try later.", Toast.LENGTH_SHORT).show()
//      }
//    }
//  })
//}