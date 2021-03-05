package com.appservice.rest.apiClients

import com.framework.rest.BaseApiClient

class BoostKitDevApiClient : BaseApiClient() {

  companion object {
    val shared = BoostKitDevApiClient()
  }
}