package com.inventoryorder.rest.apiClients

import com.framework.rest.BaseApiClient

class WebActionBoostKitApiClient : BaseApiClient(true) {

  companion object {
    val shared = WebActionBoostKitApiClient()
  }
}