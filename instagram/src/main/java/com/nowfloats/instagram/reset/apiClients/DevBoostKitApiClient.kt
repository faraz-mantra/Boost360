package com.nowfloats.instagram.reset.apiClients

import com.framework.rest.BaseApiClient

class DevBoostKitApiClient : BaseApiClient(true) {

  companion object {
    val shared = DevBoostKitApiClient()
  }
}