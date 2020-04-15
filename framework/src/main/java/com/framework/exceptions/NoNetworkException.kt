package com.framework.exceptions

class NoNetworkException : BaseException {

  internal constructor() : super("Internet connection not available.")

  internal constructor(message: String) : super(message)
}
