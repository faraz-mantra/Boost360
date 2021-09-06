package dev.patrickgold.florisboard.customization.network.client

import com.framework.rest.BaseApiClient

class BoostFloatApiClient : BaseApiClient() {
    companion object {
        val shared = BoostFloatApiClient()
    }
}