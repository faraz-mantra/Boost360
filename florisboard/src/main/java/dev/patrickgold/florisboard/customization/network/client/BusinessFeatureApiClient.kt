package dev.patrickgold.florisboard.customization.network.client

import com.framework.rest.BaseApiClient

class BusinessFeatureApiClient : BaseApiClient() {
    companion object {
        val shared = BusinessFeatureApiClient()
    }
}