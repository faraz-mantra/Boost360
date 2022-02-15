package com.boost.presignin.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import com.appservice.model.accountDetails.saveBanKDetail
import com.appservice.model.kycData.saveBusinessKycDetail
import com.boost.dbcenterapi.utils.DataLoader
import com.boost.presignin.model.other.AccountDetailsResponse
import com.boost.presignin.model.other.PaymentKycDataResponse
import com.boost.presignin.rest.repository.WebActionBoostKitRepository
import com.boost.presignin.rest.repository.WithFloatRepository
import com.boost.presignin.rest.repository.WithFloatTwoRepository
import com.framework.analytics.SentryController
import com.framework.models.toLiveData
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.model.channel.statusResponse.CHANNEL_STATUS_SUCCESS
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelsType
import com.onboarding.nowfloats.model.supportVideo.FeatureSupportVideoResponse
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import com.onboarding.nowfloats.rest.repositories.DeveloperBoostKitDevRepository
import com.onboarding.nowfloats.utils.SocialChannelUtils
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
        mPrefTwitter = this.baseContext.getSharedPreferences(
            PreferenceConstant.PREF_NAME_TWITTER,
            Context.MODE_PRIVATE
        )
        userId = userSessionManager?.fPID
        hitAPIs()
        return START_STICKY
    }

    private fun hitAPIs() {
        registerRia()
        nfxGetSocialTokens()
        hitSelfBrandedKycAPI()
        checkUserAccountDetails()
        getAndSaveKAdminFeatureSupportVideos()

        //Migrate Upgrade DB
        DataLoader.loadMarketPlaceData(application,userSessionManager?.fP_AppExperienceCode,userSessionManager?.fpTag)
    }

    private fun checkUserAccountDetails() {
        WithFloatRepository.checkUserAccount(userSessionManager?.fPID, clientId).toLiveData()
            .observeForever {
                val data = it as? AccountDetailsResponse
                if (it.isSuccess()) {
                    if (data?.result?.bankAccountDetails != null) {
                        data?.result?.bankAccountDetails?.saveBanKDetail()
                        userSessionManager?.setAccountSave(true)
                    } else userSessionManager?.setAccountSave(false)
                }
            }
    }

    private fun hitSelfBrandedKycAPI() {
        WebActionBoostKitRepository.getSelfBrandedKyc(query = getQuery()).toLiveData()
            .observeForever {
                val paymentKycDataResponse = it as? PaymentKycDataResponse
                if (it.isSuccess() && paymentKycDataResponse?.data.isNullOrEmpty().not()) {
                    userSessionManager?.isSelfBrandedKycAdd = true
                    paymentKycDataResponse?.data?.first()?.saveBusinessKycDetail()
                }
            }
    }

    private fun registerRia() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("registerRia", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            val params = HashMap<String?, String?>()
            params["Channel"] = token
            params["UserId"] = userId
            params["DeviceType"] = "ANDROID"
            params["clientId"] = clientId
            WithFloatTwoRepository.post_RegisterRia(params).toLiveData().observeForever {
                if (it.isSuccess()) Log.d("Register Ria", "registerRia: success")
                else Log.d("Register Ria", "registerRia: failed")
            }
        })
    }

    private fun nfxGetSocialTokens() {
        ChannelRepository.getChannelsStatus(userSessionManager?.fPID).toLiveData().observeForever {
            val nfxGetTokensResponse = it as? ChannelAccessStatusResponse
            if (it.isSuccess() && nfxGetTokensResponse?.channels != null) {
                SocialChannelUtils.storeSocialStatus(nfxGetTokensResponse.channels!!)
            }
        }
    }



    private fun getQuery(): String? {
        return try {
            JSONObject().apply { put("fpTag", userSessionManager?.fpTag) }.toString()
        } catch (e: JSONException) {
            SentryController.captureException(e)
            ""
        }
    }

    private fun getAndSaveKAdminFeatureSupportVideos() {
        DeveloperBoostKitDevRepository.getSupportVideos().toLiveData().observeForever {
            val featureVideo =
                (it as? FeatureSupportVideoResponse)?.data?.firstOrNull()?.featurevideo
            if (it.isSuccess() && featureVideo.isNullOrEmpty().not()) {
                FeatureSupportVideoResponse.saveSupportVideoData(featureVideo)
            }
        }
    }
}