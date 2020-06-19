package com.framework.base

import org.json.JSONException
import org.json.JSONObject

open class BaseResponse(
    var taskcode: Int? = null,
    var status: Int? = null,
    var message: String? = null,
    var error: Throwable? = null,
    var stringResponse: String? = null,
    var arrayResponse: Array<*>? = null
) {
  fun message(): String {
    val message = message ?: ""
    return try {
      val jsonObj = JSONObject(message)
      jsonObj.getString("Message") ?: jsonObj.getString("message") ?: message
    } catch (ex: JSONException) {
      message
    }
  }
}