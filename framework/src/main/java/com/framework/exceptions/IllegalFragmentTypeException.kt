package com.framework.exceptions

class IllegalFragmentTypeException : BaseException {

  constructor() : super("Invalid fragment type")

  constructor(message: String) : super(message)
}
