package com.boost.marketplace.infra.rest.apiClients

import com.framework.rest.BaseApiClient

class WithFloatsTwoApiClient : BaseApiClient() {

  companion object {
    val shared = WithFloatsTwoApiClient()
  }
}