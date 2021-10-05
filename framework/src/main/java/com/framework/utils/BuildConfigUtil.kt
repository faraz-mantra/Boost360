package com.framework.utils

import java.lang.Exception
import java.lang.reflect.Field

object BuildConfigUtil {

  inline fun <reified T> getBuildConfigField(key: String): T? {
    return try {
      val clazz = Class.forName("com.thinksity.BuildConfig")
      val field: Field? = clazz.getDeclaredField(key)
      field?.isAccessible = true
      field?.get(clazz) as? T
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
}