package com.onboarding.nowfloats.rest.apiClients

import com.framework.rest.BaseApiClient

class WebActionsApiClient : BaseApiClient(true) {

  companion object {
    val shared = WebActionsApiClient()
  }
}