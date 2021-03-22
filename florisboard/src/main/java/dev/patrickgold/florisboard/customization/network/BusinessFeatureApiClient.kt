package dev.patrickgold.florisboard.customization.network

import com.framework.rest.BaseApiClient

class BusinessFeatureApiClient : BaseApiClient() {
    companion object {
        val shared = BusinessFeatureApiClient()
    }
}