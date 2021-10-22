package com.framework.base

import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import org.json.JSONObject
import java.io.Serializable
import java.nio.charset.Charset

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
    val message = message ?: "Something went wrong!"
    return try {
      val jsonObj = JSONObject(message)
      jsonObj.getString("Message") ?: jsonObj.getString("message") ?: message
    } catch (ex: Exception) {
      message
    }
  }

  fun errorMessage(): String? {
    val message = message?:"Something went wrong!"
    return try {
      val jsonObj = JSONObject(message)
      val error = jsonObj.getJSONObject("Error")
      error.getString("ErrorDescription") ?: jsonObj.getString("errorDescription") ?: message
    } catch (ex: Exception) {
      message
    }
  }

  fun errorNMessage(): String? {
    val message = message?:"Something went wrong!"
    return try {
      val jsonObj = JSONObject(message)
      val error = jsonObj.getJSONObject("Error").getJSONObject("ErrorList")
      return error.getString("EXCEPTION")
    } catch (ex: Exception) {
      message
    }
  }

  fun isSuccess(): Boolean {
    return status == 200 || status == 201 || status == 202 || status == 204
  }

  fun parseStringResponse(): String? {
    return try {
      val source: BufferedSource? = responseBody?.source()
      source?.request(Long.MAX_VALUE)
      val buffer: Buffer? = source?.buffer
      buffer?.clone()?.readString(Charset.forName("UTF-8"))
    } catch (e: Exception) {
      e.printStackTrace()
      ""
    }
  }

  fun parseResponse(): Boolean {
    return try {
      val source: BufferedSource? = responseBody?.source()
      source?.request(Long.MAX_VALUE)
      val buffer: Buffer? = source?.buffer
      val responseBodyString: String? = buffer?.clone()?.readString(Charset.forName("UTF-8"))
      responseBodyString.toBoolean()
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }
}