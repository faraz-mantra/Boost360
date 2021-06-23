package com.onboarding.nowfloats.rest.apiClients

import com.framework.rest.BaseApiClient

class GMBApiClient : BaseApiClient(true) {

  companion object {
    val shared = GMBApiClient()
  }
}