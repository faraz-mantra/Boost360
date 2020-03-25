package com.onboarding.nowfloats.rest

import com.framework.rest.BaseApiClient

class ApiClient : BaseApiClient() {

  companion object {
    val shared = ApiClient()
  }
}