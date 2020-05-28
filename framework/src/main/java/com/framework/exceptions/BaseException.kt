package com.framework.exceptions

open class BaseException : Exception {

  internal constructor() : super()
  internal constructor(message: String) : super(message)

  override fun toString(): String {
    return message ?: super.toString()
  }
}
