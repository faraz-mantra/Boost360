package com.boost.presignin.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.boost.presignin.model.other.AccountDetailsResponse
import com.boost.presignin.model.other.PaymentKycDataResponse
import com.boost.presignin.rest.repository.WebActionBoostKitRepository
import com.boost.presignin.rest.repository.WithFloatRepository
import com.boost.presignin.rest.repository.WithFloatTwoRepository
import com.framework.models.toLiveData
import com.framework.pref.Key_Preferences.PREF_KEY_TWITTER_LOGIN
import com.framework.pref.Key_Preferences.PREF_USER_NAME
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.google.firebase.iid.FirebaseInstanceId
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.model.channel.isFacebookPage
import com.onboarding.nowfloats.model.channel.isTwitterChannel
import com.onboarding.nowfloats.model.channel.statusResponse.CHANNEL_STATUS_SUCCESS
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelsType
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class APIService : Service() {

  private var mPrefTwitter: SharedPreferences? = null
  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  var userId: String? = null
  var userSessionManager: UserSessionManager? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    userSessionManager = UserSessionManager(this.baseContext)
    mPrefTwitter = this.baseContext.getSharedPreferences(PreferenceConstant.PREF_NAME_TWITTER, Context.MODE_PRIVATE)
    userId = userSessionManager?.fPID
    hitAPIs()
    return START_STICKY
  }

  private fun hitAPIs() {
    registerRia()
    nfxGetSocialTokens()
    hitSelfBrandedKycAPI()
    checkUserAccountDetails()
  }

  private fun checkUserAccountDetails() {
    WithFloatRepository.checkUserAccount(userSessionManager?.fPID, clientId).toLiveData().observeForever {
      val data = it as? AccountDetailsResponse
      if (it.isSuccess()) {
        if (!(data?.result != null && data.result?.bankAccountDetails != null)) userSessionManager?.setAccountSave(false) else userSessionManager?.setAccountSave(true)
      }
    }
  }

  private fun hitSelfBrandedKycAPI() {
    WebActionBoostKitRepository.getSelfBrandedKyc(getQuery()).toLiveData().observeForever {
      val paymentKycDataResponse = it as? PaymentKycDataResponse
      paymentKycDataResponse?.data
      if (it.isSuccess()) {
        userSessionManager?.isSelfBrandedKycAdd = paymentKycDataResponse != null || paymentKycDataResponse?.data.isNullOrEmpty().not()
      }
    }
  }

  private fun registerRia() {
    val params = HashMap<String?, String?>()
    params["Channel"] = FirebaseInstanceId.getInstance().token
    params["UserId"] = userId
    params["DeviceType"] = "ANDROID"
    params["clientId"] = clientId

    WithFloatTwoRepository.post_RegisterRia(params).toLiveData().observeForever {
      if (it.isSuccess()) Log.d("Register Ria", "registerRia: success")
      else Log.d("Register Ria", "registerRia: failed")
    }
  }

  private fun nfxGetSocialTokens() {
    ChannelRepository.getChannelsStatus(userSessionManager?.fPID).toLiveData().observeForever {
      val nfxGetTokensResponse = it as? ChannelAccessStatusResponse
      if (it.isSuccess() && nfxGetTokensResponse?.channels != null) {
        setSharePrefDataFpPageAndTwitter(nfxGetTokensResponse.channels!!)
      }
    }
  }

  private fun setSharePrefDataFpPageAndTwitter(channelsAccessToken: ChannelsType?) {
    val editorFp = userSessionManager?.pref?.edit()
    editorFp?.putBoolean("fbShareEnabled", false)
    editorFp?.putString("fbAccessId", null)
    editorFp?.putBoolean("fbPageShareEnabled", false)
    editorFp?.putString(PreferenceConstant.KEY_FACEBOOK_NAME, "")
    editorFp?.putString("fbPageAccessId", null)
    editorFp?.putInt("fbStatus", 0)
    val fpPage = channelsAccessToken?.facebookpage
    if (fpPage != null && fpPage.status.equals(CHANNEL_STATUS_SUCCESS, true)) {
      editorFp?.putString(PreferenceConstant.KEY_FACEBOOK_PAGE, fpPage.account?.accountName ?: "")
      editorFp?.putBoolean(PreferenceConstant.FP_PAGE_SHARE_ENABLED, true)
      editorFp?.putInt(PreferenceConstant.FP_PAGE_STATUS, 1)
      editorFp?.putString("fbPageAccessId", fpPage.account?.accountId)
    } else {
      editorFp?.putString(PreferenceConstant.KEY_FACEBOOK_PAGE, null)
      editorFp?.putBoolean(PreferenceConstant.FP_PAGE_SHARE_ENABLED, false)
      editorFp?.putInt(PreferenceConstant.FP_PAGE_STATUS, 0)
    }
    val timeLine = channelsAccessToken?.facebookusertimeline
    if (timeLine != null && timeLine.status.equals(CHANNEL_STATUS_SUCCESS, true)) {
      editorFp?.putString(PreferenceConstant.KEY_FACEBOOK_NAME, timeLine.account?.accountName)
      if (timeLine.account?.accountName.isNullOrEmpty().not()) editorFp?.putBoolean("fbShareEnabled", true)
      editorFp?.putString("fbAccessId", timeLine.account?.accountId)
    }
    editorFp?.apply()

    val twitter = channelsAccessToken?.twitter
    val editorTwitter = mPrefTwitter?.edit()
    if (twitter != null && twitter.status.equals(CHANNEL_STATUS_SUCCESS, true)) {
      editorTwitter?.putString(PreferenceConstant.TWITTER_USER_NAME, twitter.account?.accountName)
      editorTwitter?.putBoolean(PreferenceConstant.PREF_KEY_TWITTER_LOGIN, true)
    } else {
      editorTwitter?.putString(PreferenceConstant.TWITTER_USER_NAME, null)
      editorTwitter?.putBoolean(PreferenceConstant.PREF_KEY_TWITTER_LOGIN, false)
    }
    editorTwitter?.apply()
  }

  private fun getQuery(): String? {
    return try {
      JSONObject().apply { put("fpTag", userSessionManager?.fpTag) }.toString()
    } catch (e: JSONException) {
      ""
    }
  }
}