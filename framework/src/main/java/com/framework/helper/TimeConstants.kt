package com.framework.helper

import androidx.annotation.IntDef

object TimeConstants {

  const val MSEC = 1
  const val SEC = 1000
  const val MIN = 60000
  const val HOUR = 3600000
  const val DAY = 86400000

  @IntDef(MSEC, SEC, MIN, HOUR, DAY)
  @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
  annotation class Unit
}
