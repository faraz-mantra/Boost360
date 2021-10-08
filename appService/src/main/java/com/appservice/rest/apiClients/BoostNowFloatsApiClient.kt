package com.appservice.rest.apiClients

import com.framework.rest.BaseApiClient

class BoostNowFloatsApiClient : BaseApiClient(true) {

  companion object {
    val shared = BoostNowFloatsApiClient()
  }
}