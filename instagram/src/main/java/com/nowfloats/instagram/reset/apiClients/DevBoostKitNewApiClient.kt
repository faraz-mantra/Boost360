package com.nowfloats.instagram.reset.apiClients

import com.framework.rest.BaseApiClient

class DevBoostKitNewApiClient : BaseApiClient(true) {

  companion object {
    val shared = DevBoostKitNewApiClient()
  }
}