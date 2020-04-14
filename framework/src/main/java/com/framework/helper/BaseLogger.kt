package com.framework.helper

import android.util.Log

object BaseLogger {

  fun debug(ob: Any, e: Throwable?) {
    log(LogType.DEBUG, ob, e)
  }

  fun debug(ob: Any, message: String) {
    log(
            LogType.DEBUG,
            ob,
            message
    )
  }

  fun verbose(ob: Any, e: Throwable?) {
    log(
            LogType.VERBOSE,
            ob,
            e
    )
  }

  fun verbose(ob: Any, message: String) {
    log(
            LogType.VERBOSE,
            ob,
            message
    )
  }

  fun error(ob: Any, e: Throwable) {
    log(LogType.ERROR, ob, e)
  }

  fun error(ob: Any, message: String) {
    log(
            LogType.ERROR,
            ob,
            message
    )
  }

  fun warning(ob: Any, e: Throwable?) {
    log(
            LogType.WARNING,
            ob,
            e
    )
  }

  fun warning(ob: Any, message: String) {
    log(
            LogType.WARNING,
            ob,
            message
    )
  }

  private fun log(type: LogType, tagObject: Any, e: Throwable?) {
    var e = e
    if (e == null) {
      e = Throwable("Unknown error")
    }

    log(type, tagObject, e.message.toString())
  }

  private fun log(type: LogType, tagObject: Any?, message: String) {
    val tag: String
    if (tagObject == null) {
      tag = type.name
    } else {
      tag = tagObject.javaClass.name
    }

    when (type) {
      LogType.DEBUG -> Log.d(tag, message)
      LogType.VERBOSE -> Log.v(tag, message)
      LogType.ERROR -> Log.e(tag, message)
      LogType.WARNING -> Log.w(tag, message)
    }
  }

  private enum class LogType {
    DEBUG,
    VERBOSE,
    ERROR,
    WARNING
  }
}