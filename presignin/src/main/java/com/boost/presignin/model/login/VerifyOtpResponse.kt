package com.boost.presignin.model.login

import com.framework.base.BaseResponse

data class VerifyOtpResponse(
  var Error: Any? = null,
  var StatusCode: Any? = null,
  var Result: VerificationRequestResult? = null,
) : BaseResponse()