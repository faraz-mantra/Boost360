package com.dashboard.rest.apiClients

import com.framework.rest.BaseApiClient

class DevBoostKitApiClient : BaseApiClient() {

  companion object {
    val shared = DevBoostKitApiClient()
  }
}