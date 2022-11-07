package com.festive.poster.reset.apiClients

import com.framework.rest.BaseApiClient

class UsCentralNowFloatsCloudApiClient  : BaseApiClient(true) {

    companion object {
        val shared = UsCentralNowFloatsCloudApiClient()
    }
}