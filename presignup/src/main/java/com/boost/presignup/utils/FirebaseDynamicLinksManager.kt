package com.boost.presignup.utils

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.boost.presignup.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData

enum class DynamicLinkParams {
  fpId,
  fpTag,
  viewType,
  day,
  referrer,
  buyItemKey
}

class FirebaseDynamicLinksManager {

  companion object {
    @JvmField
    val instance = FirebaseDynamicLinksManager()
  }

  fun parseDeepLink(activity: Activity, onResult: (Exception?, HashMap<DynamicLinkParams, String>?) -> Unit) {
    FirebaseAnalytics.getInstance(activity)
    FirebaseDynamicLinks.getInstance()
        .getDynamicLink(activity.intent)
        .addOnSuccessListener(activity) { pendingDynamicLinkData ->
          onResult(null, getDynamicLinkParams(pendingDynamicLinkData))
        }.addOnFailureListener(activity) { e ->
          e.printStackTrace()
          onResult(e, null)
        }
  }

  private fun getDynamicLinkParams(link: PendingDynamicLinkData?): HashMap<DynamicLinkParams, String> {
    val deepLink: Uri? = link?.link
    Log.d("TAG", deepLink.toString())
    return getURILinkParams(deepLink)
  }

  fun getURILinkParams(deepLink: Uri?): HashMap<DynamicLinkParams, String> {
    val map = HashMap<DynamicLinkParams, String>()
    if (deepLink?.queryParameterNames == null) return map

    for (param in deepLink.queryParameterNames) {
      try {
        val key = DynamicLinkParams.valueOf(param)
        val value = deepLink.getQueryParameter(param)

        if (value != null) {
          if (key == DynamicLinkParams.referrer) {
            for (keyValuePairString in deepLink.getQueryParameter(param)?.split("&") ?: ArrayList()) {
              val pair = keyValuePairString.split("=")
              map[DynamicLinkParams.valueOf(pair.first())] = pair.last()
            }
          } else {
            map[key] = value
          }
        }
      } catch (e: Exception) {
        Log.e(FirebaseDynamicLinksManager::class.java.name, e.localizedMessage ?: "")
      }
    }
    return map
  }

  fun createReferralLink(onResult: (Exception?, String?) -> Unit) {
    val uid = "PreferencesUtils.instance.gpuid"
    val link = "${"StoreUrl.PLAY.toString()"}&referral_code=$uid"
    FirebaseDynamicLinks.getInstance().createDynamicLink().setLink(Uri.parse(link)).setDomainUriPrefix("https://nowfloats.page.link")
        .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.biz2.nowfloats")
//            .setMinimumVersion(BuildConfig.VERSION_CODE)
            .build())
        .buildShortDynamicLink().addOnSuccessListener { shortDynamicLink ->
          onResult(null, shortDynamicLink.shortLink.toString())
        }.addOnFailureListener { exception -> onResult(exception, null) }

  }

//    fun createDynamicLink(): String{
//        val user = FirebaseAuth.getInstance().currentUser
//        val uid = PreferencesUtils.instance.gpuid
//        val link = "https://play.google.com/store/apps/details?id=com.gromo.partner&referralCode=$uid"
//        var mInvitationUrl: Uri? = null
//        FirebaseDynamicLinks.getInstance().createDynamicLink().setLink(Uri.parse(link))
//                .setDynamicLinkDomain("https://gromo.page.link")
//                .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.gromo.partner")
//                        .setMinimumVersion(BuildConfig.VERSION_CODE)
//                        .build())
//                .buildShortDynamicLink().addOnSuccessListener {
//                    shortDynamicLink ->
//                    mInvitationUrl = shortDynamicLink.shortLink
//                }
//
//        return mInvitationUrl.toString()
//    }

//    fun sendInvitation(){
//        val referrerName = FirebaseAuth.getInstance().currentUser?.displayName
//        val subject = String.format("%s has invited you to download gromo", referrerName)
//        val invitationLink = createDynamicLink()
//        val msg = "Use my referrer link: $invitationLink"
//        val msgHtml = String.format("<p>Use my " +
//                "<a href=\"%s\">referrer link</a>!</p>", invitationLink)
//    }

}
