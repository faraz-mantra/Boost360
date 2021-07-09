package com.onboarding.nowfloats.model.uploadfile

import com.framework.base.BaseRequest
import okhttp3.RequestBody

data class UploadFileProfileRequest(
    var clientId: String? = null,
    var loginId: String? = null,
    var fileName: String? = null,
    var requestBody: RequestBody? = null,
) : BaseRequest()