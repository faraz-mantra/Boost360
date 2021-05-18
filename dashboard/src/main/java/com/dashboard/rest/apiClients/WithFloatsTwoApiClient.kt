package com.dashboard.rest.apiClients

import com.framework.rest.BaseApiClient

class WithFloatsTwoApiClient : BaseApiClient() {

  companion object {
    val shared = WithFloatsTwoApiClient()
  }
}