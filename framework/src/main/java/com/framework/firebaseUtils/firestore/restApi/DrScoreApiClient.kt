package com.framework.firebaseUtils.firestore.restApi

import com.framework.rest.BaseApiClient

class DrScoreApiClient : BaseApiClient(true) {

  companion object {
    val shared = DrScoreApiClient()
  }
}