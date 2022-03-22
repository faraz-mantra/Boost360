package com.appservice.rest.apiClients

import com.framework.rest.BaseApiClient

class RiaMemoryWithFloatsApiClient : BaseApiClient() {

    companion object {
        val shared = RiaMemoryWithFloatsApiClient()
    }
}