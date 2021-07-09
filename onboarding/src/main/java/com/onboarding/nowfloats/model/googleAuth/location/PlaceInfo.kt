package com.onboarding.nowfloats.model.googleAuth.location

import java.io.Serializable

data class PlaceInfo(
    val name: String? = null,
    val placeId: String? = null
) : Serializable