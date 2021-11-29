package com.onboarding.nowfloats.rest.apiClients

import com.framework.rest.BaseApiClient

class DeveloperBoostKitDevApiClient : BaseApiClient(true) {

    companion object {
        val shared = DeveloperBoostKitDevApiClient()
    }
}