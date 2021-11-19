package dev.patrickgold.florisboard.customization.network.client

import com.framework.rest.BaseApiClient

class NowFloatApiClient : BaseApiClient() {

  companion object {
    val shared = NowFloatApiClient()
  }
}