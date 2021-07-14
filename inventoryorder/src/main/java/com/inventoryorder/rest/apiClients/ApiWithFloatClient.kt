package com.inventoryorder.rest.apiClients

import com.framework.rest.BaseApiClient

class ApiWithFloatClient : BaseApiClient() {
  companion object {
    val shared = ApiWithFloatClient()
  }
}