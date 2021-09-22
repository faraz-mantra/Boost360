package com.inventoryorder.rest.apiClients

import com.framework.rest.BaseApiClient

open class AssuredPurchaseClient : BaseApiClient() {
  companion object {
    val shared = AssuredPurchaseClient()
  }
}