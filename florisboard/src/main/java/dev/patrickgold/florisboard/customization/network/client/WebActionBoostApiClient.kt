package dev.patrickgold.florisboard.customization.network.client

import com.framework.rest.BaseApiClient

class WebActionBoostApiClient : BaseApiClient(true) {
    companion object {
        val shared = WebActionBoostApiClient()
    }
}