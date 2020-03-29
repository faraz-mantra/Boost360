package com.onboarding.nowfloats.rest.apiClients

import com.framework.rest.BaseApiClient

class NfxApiClient : BaseApiClient() {

  companion object {
    val shared = NfxApiClient()
  }
}