package com.onboarding.nowfloats.rest.apiClients

import com.framework.rest.BaseApiClient

class BoostFloatClient : BaseApiClient() {

  companion object {
    val shared = BoostFloatClient()
  }
}