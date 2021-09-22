package com.framework.analytics

import io.sentry.Sentry

object SentryController {

    fun captureException(){
        try {
            throw Exception("This is a test.")
        } catch (e: Exception) {
            e.printStackTrace()
            Sentry.captureException(e)
        }
    }
}