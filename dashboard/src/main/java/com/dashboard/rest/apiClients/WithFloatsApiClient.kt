package com.dashboard.rest.apiClients

import com.framework.rest.BaseApiClient

class WithFloatsApiClient : BaseApiClient() {

  companion object {
    val shared = WithFloatsApiClient()
  }
}