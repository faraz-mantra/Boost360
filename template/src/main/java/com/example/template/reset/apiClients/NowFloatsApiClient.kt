package com.example.template.reset.apiClients

import com.framework.rest.BaseApiClient

class NowFloatsApiClient : BaseApiClient() {

  companion object {
    val shared = NowFloatsApiClient()
  }
}