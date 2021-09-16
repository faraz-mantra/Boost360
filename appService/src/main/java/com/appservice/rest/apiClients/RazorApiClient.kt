package com.appservice.rest.apiClients

import com.framework.rest.BaseApiClient

class RazorApiClient : BaseApiClient(true) {

  companion object {
    val shared = RazorApiClient()
  }
}