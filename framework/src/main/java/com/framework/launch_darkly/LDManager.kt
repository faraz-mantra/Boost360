package com.framework.launch_darkly

import android.app.Application
import android.util.Log
import com.launchdarkly.sdk.LDUser
import com.launchdarkly.sdk.android.LDClient
import com.launchdarkly.sdk.android.LDConfig

object LDManager {
    val MOBILE_KEY = "mob-4566c0eb-5b03-4d96-be55-66a8854887a4"
    val TAG = "LaunchDarklyManager"
    var ldConfig: LDConfig? = null;
    var user: LDUser? = null;
    var ldClient: LDClient? = null;
    var context: Application? = null;

    fun init(context: Application, uniqueKey: String, email: String? = "") {
        this.context = context
        buildClient()
        buildUser(uniqueKey, email)
        initClient()
    }


    private fun buildClient() {
        ldConfig = LDConfig.Builder()
                .mobileKey(MOBILE_KEY)
                .build()

    }

    private fun buildUser(uniqueKey: String, email: String? = "") {
        user = LDUser.Builder(uniqueKey)
                .email(email)
                .build()
    }

    private fun initClient() {
        ldClient = LDClient.init(context, ldConfig, user, 0)
    }

    fun showFeature(key: String? = "", default: Boolean? = true): Boolean? {
        ldClient?.let { return it.boolVariation(key!!, default!!) }
        Log.e(TAG, "Launch Darkly Manager is not initialized yet")
        return default
    }

}