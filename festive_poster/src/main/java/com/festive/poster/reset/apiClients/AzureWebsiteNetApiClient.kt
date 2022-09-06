package com.festive.poster.reset.apiClients

import com.framework.rest.BaseApiClient

class AzureWebsiteNetApiClient : BaseApiClient() {

  companion object {
    val shared = AzureWebsiteNetApiClient()
  }
}