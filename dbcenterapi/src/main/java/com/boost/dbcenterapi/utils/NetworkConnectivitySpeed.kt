package com.boost.dbcenterapi.utils

import android.content.Context
import android.net.ConnectivityManager

import android.net.NetworkInfo

import android.telephony.TelephonyManager


object NetworkConnectivitySpeed {

  private var connectionStatus = false
  var timerCallbackConst: Long = 1000

  private fun isConnectedFast(context: Context): Boolean {
    val info = getNetworkInfo(context)
    return info != null && info.isConnected && isConnectionFast(info.type, info.subtype, context)
  }

  private fun isConnectionFast(type: Int, subType: Int, context: Context): Boolean {
    return if (type == ConnectivityManager.TYPE_WIFI) {
      true
    } else if (type == ConnectivityManager.TYPE_MOBILE) {
      println("typeconn ${subType}")
      when (subType) {
        TelephonyManager.NETWORK_TYPE_1xRTT -> false // ~ 50-100 kbps
        TelephonyManager.NETWORK_TYPE_CDMA -> false // ~ 14-64 kbps
        TelephonyManager.NETWORK_TYPE_EDGE -> false // ~ 50-100 kbps
        TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
        TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
        TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps
        TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
        TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
        TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
        TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
        TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
        TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
        TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
        TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
        TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
        TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
        else -> false
      }
    } else {
      false
    }
  }

  fun checkNetworkType(ctx: Context): Long {
    connectionStatus = isConnectedFast(ctx)
    if (!connectionStatus) {
      timerCallbackConst = 10000
      // connection is slow
    } else {
      // connection is fast
      timerCallbackConst = 1000
    }
//         return connectionStatus
    return timerCallbackConst
  }

  private fun getNetworkInfo(context: Context): NetworkInfo? {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo
  }
}