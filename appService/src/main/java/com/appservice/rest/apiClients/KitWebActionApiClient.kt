package com.appservice.rest.apiClients

import com.framework.rest.BaseApiClient

class KitWebActionApiClient : BaseApiClient() {

  companion object {
    val shared = KitWebActionApiClient()
  }
}