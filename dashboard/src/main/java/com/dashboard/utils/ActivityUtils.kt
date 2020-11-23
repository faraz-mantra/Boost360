package com.dashboard.utils

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dashboard.R
import com.dashboard.pref.Key_Preferences
import com.dashboard.pref.StoreWidgets
import com.dashboard.pref.UserSessionManager
import com.dashboard.pref.WA_KEY
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.ui.updateChannel.startFragmentActivity
import java.util.*

fun AppCompatActivity.startDigitalChannel(session: UserSessionManager) {
  try {
    val bundle = Bundle()
    val rootAlisasURI: String = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI) ?: ""
    session.setHeader(WA_KEY)
    bundle.putString(UserSessionManager.KEY_FP_ID, session.fPID)
    bundle.putString(Key_Preferences.GET_FP_DETAILS_TAG, session.fpTag)
    bundle.putString(Key_Preferences.GET_FP_EXPERIENCE_CODE, session.fP_AppExperienceCode)
    bundle.putBoolean("IsUpdate", true)
    val normalURI = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)?.toLowerCase(Locale.ROOT) + this.getString(R.string.tag_for_partners)
    if (rootAlisasURI.isNotEmpty()) bundle.putString("website_url", rootAlisasURI) else bundle.putString("website_url", normalURI)
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