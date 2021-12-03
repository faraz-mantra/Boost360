package com.onboarding.nowfloats.model.googleAuth

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

class FirebaseTokenResponse(
  @SerializedName("Result")
  val Result: String?
) : BaseResponse() {
}