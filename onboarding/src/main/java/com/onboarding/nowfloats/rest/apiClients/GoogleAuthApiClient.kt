package com.onboarding.nowfloats.rest.apiClients

import com.framework.rest.BaseApiClient

class GoogleAuthApiClient : BaseApiClient(true) {

  companion object {
    val shared = GoogleAuthApiClient()
  }
}