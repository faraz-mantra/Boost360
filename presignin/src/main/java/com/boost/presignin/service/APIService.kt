package com.boost.presignin.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.boost.presignin.model.other.AccountDetailsResponse
import com.boost.presignin.model.other.NfxGetTokensResponse
import com.boost.presignin.model.other.PaymentKycDataResponse
import com.boost.presignin.rest.repository.NfxFacebookAnalyticsRepository
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
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class APIService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    var userId: String? = null
    var userSessionManager: UserSessionManager? = null
    private val SMS_REGEX = "SMS_REGEX"
    private val CALL_LOG_TIME_INTERVAL = "CALL_LOG_INTERVAL"
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userSessionManager = UserSessionManager(this.baseContext)
        userId = userSessionManager?.userProfileId
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
        WithFloatRepository.checkUserAccount(userSessionManager?.fPID, clientId2).toLiveData().observeForever {
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
            if (it.isSuccess()) {
                Log.d("Register Ria", "registerRia: success")

            } else {
                Log.d("Register Ria", "registerRia: failed")


            }
        }
    }

    private fun nfxGetSocialTokens() {
        NfxFacebookAnalyticsRepository.getSocialTokens(userSessionManager?.fPID).toLiveData().observeForever {
            val nfxGetTokensResponse = it as? NfxGetTokensResponse
            if (it.isSuccess() && nfxGetTokensResponse != null) {
                val regexList: List<String?> = nfxGetTokensResponse.smsRegex!!
                if (regexList.isNotEmpty()) {
                    val s = TextUtils.join(",", regexList)
                    userSessionManager?.storeFPDetails(SMS_REGEX, s)
                    userSessionManager?.storeFPDetails(CALL_LOG_TIME_INTERVAL, nfxGetTokensResponse.callLogTimeInterval)
                }
                storeData(nfxGetTokensResponse)

            }
        }
    }

    private fun storeData(nfxGetTokensResponse: NfxGetTokensResponse) {
        userSessionManager?.storeBooleanDetails("fbShareEnabled", false)
        userSessionManager?.storeFacebookName(null)
        userSessionManager?.storeFPDetails("fbAccessId", null)
        userSessionManager?.storeBooleanDetails("fbPageShareEnabled", false)
        userSessionManager?.storeFacebookPage(null)
        userSessionManager?.storeFPDetails("fbPageAccessId", null)
        userSessionManager?.storeIntDetails("fbStatus", 0)
        userSessionManager?.storeIntDetails("fbPageStatus", 0)
        userSessionManager?.storeIntDetails("quikrStatus", -1)
        userSessionManager?.storeIntDetails("facebookChatStatus", 0)
        userSessionManager?.storeBooleanDetails("is_twitter_loggedin", false)
        userSessionManager?.storeFPDetails("twitter_user_name", null)
        for (model in nfxGetTokensResponse.nFXAccessTokens) {
            when {
                model.type.equals("facebookusertimeline", ignoreCase = true) -> {
                    userSessionManager?.storeFacebookName(model.userAccountName)
                    userSessionManager?.storeIntDetails("fbStatus", model.status?.toInt()!!)
                    if (userSessionManager?.facebookName.isNullOrEmpty().not()) {
                        userSessionManager?.storeBooleanDetails("fbShareEnabled", true)
                    }
                    userSessionManager?.storeFPDetails("fbAccessId", model.userAccountId)
                }
                model.type.equals("facebookpage", ignoreCase = true) -> {
                    if (TextUtils.isDigitsOnly(model.status)) userSessionManager?.storeIntDetails("fbPageStatus", model.status?.toInt()!!)
                    userSessionManager?.storeFacebookPage(model.userAccountName)
                    if (userSessionManager?.facebookPage.isNullOrEmpty().not()) {
                        userSessionManager?.storeBooleanDetails("fbPageShareEnabled", true)
                    }
                    userSessionManager?.storeFPDetails("fbPageAccessId", model.userAccountId)
                }
                model.type.equals("twitter", ignoreCase = true) -> {
                    if (model.status == "1" || model.status == "3") {
                        userSessionManager?.storeBooleanDetails(PREF_KEY_TWITTER_LOGIN, true)
                    }
                    userSessionManager?.storeFPDetails(PREF_USER_NAME, model.userAccountName)
                }
                model.type.equals("quikr", ignoreCase = true) -> {
                    userSessionManager?.storeIntDetails("quikrStatus", model.status?.toInt()!!)
                }
                model.type.equals("facebookchat", ignoreCase = true) -> {
                    userSessionManager?.storeIntDetails("facebookChatStatus", model.status?.toInt()!!)
                }
            }
        }

    }

    private fun getQuery(): String? {
        return try {
            val jsonObject = JSONObject()
            jsonObject.put("fpTag", userSessionManager?.fpTag)
            jsonObject.toString()
        } catch (e: JSONException) {
            ""
        }
    }
}