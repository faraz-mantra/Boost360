package com.boost.presignin.rest.apiClients

import com.framework.rest.BaseApiClient

class WithFloatsApiTwoClient : BaseApiClient() {

    companion object {
        val shared = WithFloatsApiTwoClient()
    }
}