package com.boost.presignin.rest.apiClients

import com.framework.rest.BaseApiClient

class WebActionBoostKitClient : BaseApiClient(true) {
  companion object {
    val shared = WebActionBoostKitClient()
  }

}