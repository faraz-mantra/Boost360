package com.boost.presignup

open class PreSignUpException : Exception {

  internal constructor() : super()
  constructor(message: String) : super(message)

  override fun toString(): String {
    return message ?: super.toString()
  }
}
