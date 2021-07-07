package com.framework.utils

import android.content.Context
import android.net.ConnectivityManager
import com.framework.BaseApplication


object NetworkUtils {

  fun isNetworkConnected(): Boolean {
    val connectivityManager =
      BaseApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo?.isConnected ?: false

  }

}
