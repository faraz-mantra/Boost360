package com.dashboard.controller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dashboard.utils.DeepLinkUtil
import com.framework.pref.UserSessionManager


class DeepLinkTransActivity : AppCompatActivity() {

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screenType=intent.extras?.getString("SCREEN_TYPE")
        val session=UserSessionManager(this)
        if (screenType != null) {
            DeepLinkUtil(this,session,null).deepLinkPage(screenType,"",false)
        }
        finish()

    }
}