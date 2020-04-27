package com.inventoryorder.rest.apiClients

import com.framework.rest.BaseApiClient

class NfxApiClient : BaseApiClient() {

  companion object {
    val shared = NfxApiClient()
  }
}