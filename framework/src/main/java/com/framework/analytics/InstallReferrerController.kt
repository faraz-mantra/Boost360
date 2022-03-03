package com.framework.analytics

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.framework.utils.PreferencesUtils
import com.framework.utils.getData
import com.framework.utils.saveData
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import android.os.Looper.getMainLooper


object InstallReferrerController {

    private const val installReferrerPref = "checkedInstallReferrer"
    private lateinit var referrerClient: InstallReferrerClient
    private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()

    fun checkInstallReferer(application: Application) {
        if(PreferencesUtils.instance.getData(installReferrerPref, false)) {
            return
        }
        backgroundExecutor.execute { getInstallReferrerClient(application) }
    }

    fun getInstallReferrerClient(application: Application) {
        referrerClient = InstallReferrerClient.newBuilder(application).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // Connection established.
                        val response: ReferrerDetails = referrerClient.installReferrer
                        val referrerUrl: String = response.installReferrer
                        val referrerClickTime: Long = response.referrerClickTimestampSeconds
                        val appInstallTime: Long = response.installBeginTimestampSeconds
                        val instantExperienceLaunched: Boolean = response.googlePlayInstantParam

//                        trackInstallReferrerForGTM(application, referrerUrl)

                        PreferencesUtils.instance.saveData(installReferrerPref, true)
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        // API not available on the current Play Store app.
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        // Connection couldn't be established.
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

//    //Tracker for Classic GA (call this if you are using Classic GA only)
//    private fun trackInstallReferrer(application: Application, referrerUrl: String) {
//        Handler(getMainLooper()).post(Runnable {
//            val receiver = CampaignTrackingReceiver()
//            val intent = Intent("com.android.vending.INSTALL_REFERRER")
//            intent.putExtra("referrer", referrerUrl)
//            receiver.onReceive(application, intent)
//        })
//    }
//
//    // Tracker for GTM + Classic GA (call this if you are using GTM + Classic GA only)
//    private fun trackInstallReferrerForGTM(application: Application, referrerUrl: String) {
//        Handler(getMainLooper()).post(Runnable {
//            val receiver = InstallReferrerReceiver()
//            val intent = Intent("com.android.vending.INSTALL_REFERRER")
//            intent.putExtra("referrer", referrerUrl)
//            receiver.onReceive(application, intent)
//        })
//    }

}