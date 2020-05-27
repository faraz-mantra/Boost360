package com.framework.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

fun convertMapToString(data: Map<String?, String?>): String? {
  //convert Map  to String
  return GsonBuilder().setPrettyPrinting().create().toJson(data)
}

fun <T> convertStringToList(strListObj: String): List<T>? {
  //convert string json to object List
  return Gson().fromJson(strListObj, object : TypeToken<List<Any?>?>() {}.type)
}

fun <T> convertStringToObj(strObj: String, classOfT: Class<T>?): T? {
  //convert string json to object
  return Gson().fromJson(strObj, classOfT as Type?)
}

fun convertStringToJsonObj(strObj: String): JsonObject? {
  //convert string json to object
  return Gson().fromJson(strObj, JsonObject::class.java)
}

fun <T> convertListObjToString(listObj: List<T>): String? {
  //convert object list to string json for
  return Gson().toJson(listObj, object : TypeToken<List<T>?>() {}.type)
}

fun convertObjToString(clsObj: Any): String? {
  //convert object  to string json
  return Gson().toJson(clsObj, object : TypeToken<Any?>() {}.type)
}