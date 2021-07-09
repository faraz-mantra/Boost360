package com.onboarding.nowfloats.model.googleAuth.location

import java.io.Serializable

data class Places(
    val placeInfos: List<PlaceInfo>? = null
) : Serializable