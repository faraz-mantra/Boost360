package com.example.template.reset.apiClients

import com.framework.rest.BaseApiClient

class WithFloatsApiClient : BaseApiClient() {

  companion object {
    val shared = WithFloatsApiClient()
  }
}