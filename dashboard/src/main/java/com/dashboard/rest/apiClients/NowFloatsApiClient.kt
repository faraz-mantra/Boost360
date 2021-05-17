package com.dashboard.rest.apiClients

import com.framework.rest.BaseApiClient

class NowFloatsApiClient : BaseApiClient() {

  companion object {
    val shared = NowFloatsApiClient()
  }
}