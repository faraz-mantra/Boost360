package com.catlogservice.rest.apiClients

import com.framework.rest.BaseApiClient

class RazorApiClient : BaseApiClient() {

  companion object {
    val shared = RazorApiClient()
  }
}