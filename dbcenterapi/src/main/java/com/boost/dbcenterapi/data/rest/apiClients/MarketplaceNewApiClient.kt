package com.boost.dbcenterapi.data.rest.apiClients

import com.framework.rest.BaseApiClient

class MarketplaceNewApiClient :BaseApiClient(true) {

  companion object{
    val shared = MarketplaceNewApiClient()
  }
}