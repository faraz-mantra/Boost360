package com.framework.base

import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

open class BaseResponse(
    var taskcode: Int? = null,
    var status: Int? = null,
    var message: String? = null,
    var error: Throwable? = null,
    var stringResponse: String? = null,
    var arrayResponse: Array<*>? = null,
    var anyResponse: Any? = null,
    var responseBody: ResponseBody? = null,
) : Serializable {

  fun message(): String {
    val message = message ?: ""
    return try {
      val jsonObj = JSONObject(message)
      jsonObj.getString("Message") ?: jsonObj.getString("message") ?: message
    } catch (ex: JSONException) {
      message
    }
  }

  fun errorMessage(): String? {
    val message = message
    return try {
      val jsonObj = JSONObject(message)
      val error = jsonObj.getJSONObject("Error")
      error.getString("ErrorDescription") ?: jsonObj.getString("errorDescription") ?: message
    } catch (ex: JSONException) {
      message
    }
  }

  fun isSuccess(): Boolean {
    return status == 200 || status == 201 || status == 202 || status == 204
  }
}