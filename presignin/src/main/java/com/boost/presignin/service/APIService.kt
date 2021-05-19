package com.boost.presignin.service

import android.app.Service
import android.content.Intent
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
import com.onboarding.nowfloats.model.channel.statusResponse.CHANNEL_STATUS_SUCCESS
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelsType
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class APIService : Service() {

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  var userId: String? = null
  var userSessionManager: UserSessionManager? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    userSessionManager = UserSessionManager(this.baseContext)
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
        storeData(nfxGetTokensResponse.channels!!)
      }
    }
  }

  private fun storeData(channelsType: ChannelsType) {
    userSessionManager?.storeBooleanDetails("fbShareEnabled", false)
    userSessionManager?.storeFacebookName(null)
    userSessionManager?.storeFPDetails("fbAccessId", null)
    userSessionManager?.storeBooleanDetails("fbPageShareEnabled", false)
    userSessionManager?.storeFacebookPage(null)
    userSessionManager?.storeFPDetails("fbPageAccessId", null)
    userSessionManager?.storeIntDetails("fbStatus", 0)
    userSessionManager?.storeBooleanDetails(PREF_KEY_TWITTER_LOGIN, false)
    userSessionManager?.storeFPDetails(PREF_USER_NAME, null)
    if (channelsType.facebookpage != null && channelsType.facebookpage!!.accountStatus.equals(CHANNEL_STATUS_SUCCESS, true)) {
      userSessionManager?.storeFacebookPage(channelsType.facebookpage?.account?.accountName ?: "")
      userSessionManager?.storeBooleanDetails("fbPageShareEnabled", true)
      userSessionManager?.storeFPDetails("fbPageAccessId", channelsType.facebookpage?.account?.accountId ?: "")
    }
    if (channelsType.facebookusertimeline != null && channelsType.facebookpage!!.accountStatus.equals(CHANNEL_STATUS_SUCCESS, true)) {
      userSessionManager?.storeFacebookName(channelsType.facebookusertimeline?.account?.accountName ?: "")
      userSessionManager?.storeFPDetails("fbStatus", channelsType.facebookusertimeline?.status ?: "")
      userSessionManager?.storeBooleanDetails("fbShareEnabled", true)
      userSessionManager?.storeFPDetails("fbAccessId", channelsType.facebookusertimeline?.account?.accountId ?: "")
    }
    if (channelsType.twitter != null && channelsType.facebookpage!!.accountStatus.equals(CHANNEL_STATUS_SUCCESS, true)) {
      userSessionManager?.storeBooleanDetails(PREF_KEY_TWITTER_LOGIN, true)
      userSessionManager?.storeFPDetails(PREF_USER_NAME, channelsType.twitter?.account?.accountName ?: "")
    }
  }

  private fun getQuery(): String? {
    return try {
      JSONObject().apply { put("fpTag", userSessionManager?.fpTag) }.toString()
    } catch (e: JSONException) {
      ""
    }
  }
}