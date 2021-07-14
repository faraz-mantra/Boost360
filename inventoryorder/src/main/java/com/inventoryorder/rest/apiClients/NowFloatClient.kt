package com.inventoryorder.rest.apiClients

import com.framework.rest.BaseApiClient

class NowFloatClient : BaseApiClient() {
  companion object {
    val shared = NowFloatClient()
  }
}