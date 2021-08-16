package com.marketplace.rest.apiClients

import com.framework.rest.BaseApiClient

class DeveloperBoostKitApiClient : BaseApiClient(true) {

  companion object {
    val shared = DeveloperBoostKitApiClient()
  }
}