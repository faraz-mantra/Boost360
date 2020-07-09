package com.onboarding.nowfloats.rest.apiClients

import com.framework.rest.BaseApiClient

class GMBApiClient : BaseApiClient() {

  companion object {
    val shared = GMBApiClient()
  }
}