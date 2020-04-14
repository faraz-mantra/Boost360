package com.framework.exceptions

class ParseException : BaseException {

  internal constructor() : super("Error parsing")

  internal constructor(message: String) : super(message)

}
