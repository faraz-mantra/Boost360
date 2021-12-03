package com.boost.marketplace.infra.api.interceptor

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.infra.utils.LogUtils
import okhttp3.*
import java.lang.Exception
import java.net.SocketTimeoutException
import javax.inject.Inject

class ErrorHandlingInterceptor @Inject constructor(
    private val application: Context,
    sharedPrefs: SharedPrefs
) : Interceptor {
    private val sharedPrefs: SharedPrefs
    private val somethingWentWrong = "Something went wrong : "
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        var response: Response? = null
        try {
            response = chain.proceed(request)

            // checking for encrypted object
            if (response!!.code >= 200 && response.code < 300) {
                if (response.body != null) {
                    logEvent(
                        Event.event_network_success,
                        request.url.toString(),
                        response.body
                    )
                }
            } else if (response.code == 401) {
                if (response.body != null) {
                    logEvent(
                        Event.event_fail,
                        request.url.toString(),
                        response.body
                    )
                }
            } else {
                if (response.body != null) {
                    logEvent(
                        Event.event_fail,
                        request.url.toString(),
                        response.body
                    )
                }
            }
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
        } catch (e: Exception) {
            LogUtils.error(TAG, e.localizedMessage)
            backgroundThreadShortToast(
                application,
                application.getString(R.string.something_went_wrong)
            )
        }
        return response!!
    }

    private fun logEvent(eventType: String, url: String, responseBody: ResponseBody?) {
        Handler(Looper.getMainLooper()).post {
            LogUtils.debug("eventType", eventType)
            LogUtils.debug("url", url)
            LogUtils.debug("responseBody", responseBody.toString())
        }
    }

    interface Event {
        companion object {
            const val event_click = "droid_click"
            const val event_render = "droid_render"
            const val event_network = "droid_network"
            const val event_network_success = "droid_network_success"
            const val event_fail = "droid_network_fail"
        }
    }


    companion object {
        val TAG = this::class.java.simpleName
        private fun backgroundThreadShortToast(context: Context?, msg: String?) {
            LogUtils.error(TAG, "error message >>>>>>$msg")
            if (context != null && msg != null) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        context,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    init {
        this.sharedPrefs = sharedPrefs
    }
}