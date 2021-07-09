package com.appservice.rest.apiClients


import com.framework.rest.BaseApiClient

class NowfloatsApiClient : BaseApiClient() {

    companion object {
        val shared = NowfloatsApiClient()
    }
}