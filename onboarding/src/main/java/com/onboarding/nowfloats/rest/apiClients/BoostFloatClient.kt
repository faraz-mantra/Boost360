package com.onboarding.nowfloats.rest.apiClients

import com.framework.rest.BaseApiClient

class BoostFloatClient : BaseApiClient(true) {

  companion object {
    val shared = BoostFloatClient()
  }
}