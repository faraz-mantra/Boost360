package com.dashboard.rest.apiClients

import com.framework.rest.BaseApiClient

class DevBoostKitNewApiClient : BaseApiClient(true) {

  companion object {
    val shared = DevBoostKitNewApiClient()
  }
}