package com.inventoryorder.model

import java.io.Serializable

data class PreferenceData(
    val clientId: String? = null,
    val userProfileId: String? = null,
    val authorization: String? = null,
    val fpTag: String? = null
) : Serializable