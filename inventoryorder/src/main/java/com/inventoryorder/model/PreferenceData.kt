package com.inventoryorder.model

import java.io.Serializable

const val AUTHORIZATION_3 = "59f6bc18dd304110e0972777"

data class PreferenceData(
    val clientId: String? = null,
    val userProfileId: String? = null,
    val authorization: String? = null,
    val fpTag: String? = null,
    val userPrimaryMobile: String? = null
) : Serializable