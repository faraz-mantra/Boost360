package com.appservice.rest.apiClients

import com.framework.rest.BaseApiClient

class LogotronBuildMyLogoApiClient  : BaseApiClient(true) {

    companion object {
        val shared = LogotronBuildMyLogoApiClient()
    }
}