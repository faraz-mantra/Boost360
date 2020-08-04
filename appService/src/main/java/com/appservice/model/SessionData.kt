package com.appservice.model

import java.io.Serializable

enum class StatusKyc {
  CUSTOM_PAYMENTGATEWAY
}

data class SessionData(
    var fpId: String? = null,
    var clientId: String? = null,
    var fpTag: String? = null,
    var userProfileId: String? = null,
    var experienceCode: String? = null,
    var fpLogo: String? = null,
    var fpEmail: String? = null,
    var fpNumber: String? = null,
    val auth_1: String = "597ee93f5d64370820a6127c",
    val auth_2: String = "59ca26afdd30411900b6a8db"
) : Serializable