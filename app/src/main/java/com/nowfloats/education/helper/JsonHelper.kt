package com.nowfloats.education.helper

import com.google.gson.Gson

/**
 * Created by Mayank on 13-Jun-2017.
 */
object JsonHelper {

  inline fun <reified T> jsonToKt(jsonString: String): T =
    Gson().fromJson(jsonString, T::class.java)

  fun KtToJson(obj: Any): String = Gson().toJson(obj)

  inline fun <reified T> jsonToKtList(jsonString: String): List<T> =
    Gson().fromJson<List<T>>(jsonString, T::class.java)

}