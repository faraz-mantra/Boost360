package com.dashboard.rest.apiClients

import com.framework.rest.BaseApiClient

class UsCentralNowFloatsCloudApiClient  : BaseApiClient(true) {

    companion object {
        val shared = UsCentralNowFloatsCloudApiClient()
    }
}