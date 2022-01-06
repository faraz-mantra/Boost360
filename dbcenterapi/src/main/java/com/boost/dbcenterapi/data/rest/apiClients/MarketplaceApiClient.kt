package com.boost.dbcenterapi.data.rest.apiClients

import com.framework.rest.BaseApiClient

class MarketplaceApiClient :BaseApiClient() {

  companion object{
    val shared = MarketplaceApiClient()
  }
}