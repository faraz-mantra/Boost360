package com.appservice.rest.apiClients

import com.framework.rest.BaseApiClient

class KitWebActionApiClient : BaseApiClient(true) {

  companion object {
    val shared = KitWebActionApiClient()
  }
}