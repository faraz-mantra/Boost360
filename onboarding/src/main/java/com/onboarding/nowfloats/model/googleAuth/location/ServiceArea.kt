package com.onboarding.nowfloats.model.googleAuth.location

import java.io.Serializable

data class ServiceArea(
  val businessType: String? = null,
  val places: Places? = null
) : Serializable