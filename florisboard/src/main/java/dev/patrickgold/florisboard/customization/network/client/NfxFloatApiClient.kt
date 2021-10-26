package dev.patrickgold.florisboard.customization.network.client

import com.framework.rest.BaseApiClient

class NfxFloatApiClient : BaseApiClient() {
    companion object {
        val shared = NfxFloatApiClient()
    }
}