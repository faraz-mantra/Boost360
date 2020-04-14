package com.framework.exceptions

class InvalidRequestException : BaseException {

  internal constructor() : super("Invalid Request")

  internal constructor(message: String) : super(message)

}
