package com.framework.exceptions

class NoNetworkException : BaseException {

  internal constructor() : super("No network")

  internal constructor(message: String) : super(message)
}
