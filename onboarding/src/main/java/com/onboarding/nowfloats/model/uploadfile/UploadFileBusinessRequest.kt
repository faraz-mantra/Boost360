package com.onboarding.nowfloats.model.uploadfile

import com.framework.base.BaseRequest
import okhttp3.RequestBody

data class UploadFileBusinessRequest(
  var clientId: String? = null,
  var fpId: String? = null,
  var identifierType: String? = null,
  var fileName: String? = null,
  var requestBody: RequestBody? = null,
) : BaseRequest() {

  enum class Type {
    DEFAULT, SINGLE, MULTI;
  }
}