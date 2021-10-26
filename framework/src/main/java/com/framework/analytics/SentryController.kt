package com.framework.analytics

import android.util.Log
import com.framework.pref.UserSessionManager
import io.sentry.Sentry
import io.sentry.protocol.User

object SentryController {

    private const val TAG = "SentryController"

    fun captureException(e:Exception){
        Sentry.captureException(e)
    }


    fun setUser(session:UserSessionManager){
        val user = User()
        user.id=session.fPID
        user.username =session.fpTag
        Sentry.setUser(user)
    }
}