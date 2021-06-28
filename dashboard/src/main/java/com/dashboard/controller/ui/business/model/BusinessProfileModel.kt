package com.dashboard.controller.ui.business.model

import java.io.Serializable

data class BusinessProfileModel(
    var businessName: String? = null,
    var businessDesc: String? = null,
    var businessImage: String? = null,
    var businessCategory: String? = null,
) : Serializable