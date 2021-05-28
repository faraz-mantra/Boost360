package com.inventoryorder.utils

import android.net.Uri
import android.util.Log

enum class DynamicLinkParams {
  fpId,
  fpTag,
  viewType,
  day,
  referrer,
  buyItemKey
}

class DynamicLinksManager {

  companion object {
    @JvmField
    val instance = DynamicLinksManager()
  }


  fun getURILinkParams(deepLink: Uri?): HashMap<DynamicLinkParams, String> {
    val map = HashMap<DynamicLinkParams, String>()
    if (deepLink?.queryParameterNames == null) return map

    for (param in deepLink.queryParameterNames) {
      try {
        val key = DynamicLinkParams.valueOf(param)
        val value = deepLink.getQueryParameter(param)
        if (value != null) {
          if (key == DynamicLinkParams.referrer) {
            for (keyValuePairString in deepLink.getQueryParameter(param)?.split("&") ?: ArrayList()) {
              val pair = keyValuePairString.split("=")
              map[DynamicLinkParams.valueOf(pair.first())] = pair.last()
            }
          } else {
            map[key] = value
          }
        }
      } catch (e: Exception) {
        Log.e(DynamicLinksManager::class.java.name, e.localizedMessage ?: "")
      }
    }
    return map
  }

}

