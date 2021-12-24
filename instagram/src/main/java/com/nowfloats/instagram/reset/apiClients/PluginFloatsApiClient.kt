package com.nowfloats.instagram.reset.apiClients

import com.framework.rest.BaseApiClient

class PluginFloatsApiClient : BaseApiClient() {

  companion object {
    val shared = PluginFloatsApiClient()
  }
}