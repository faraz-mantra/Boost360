package com.framework.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

fun convertMapToString(data: Map<String?, String?>): String? {
  return GsonBuilder().setPrettyPrinting().create().toJson(data)
}

fun convertStringToJsonObj(strObj: String): JsonObject? {
  return Gson().fromJson(strObj, JsonObject::class.java)
}

fun <T> convertListObjToString(listObj: List<T>): String? {
  return Gson().toJson(listObj, object : TypeToken<List<T>?>() {}.type)
}

inline fun <reified T> convertStringToList(json: String): List<T>? {
  val type = TypeToken.getParameterized(ArrayList::class.java, T::class.java).type
  return Gson().fromJson<ArrayList<T>>(json, type)
}

inline fun <reified T> convertStringToObj(json: String?): T? {
  return Gson().fromJson(json, object : TypeToken<T>() {}.type)
}

inline fun <reified T> convertObjToString(clsObj: T): String? {
  return Gson().toJson(clsObj, object : TypeToken<T?>() {}.type)
}

