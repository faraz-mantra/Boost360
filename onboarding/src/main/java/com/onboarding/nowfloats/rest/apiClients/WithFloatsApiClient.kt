package com.onboarding.nowfloats.rest.apiClients

import com.framework.rest.BaseApiClient

class WithFloatsApiClient : BaseApiClient() {

  companion object {
    val shared = WithFloatsApiClient()
  }
}