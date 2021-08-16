package com.onboarding.nowfloats.model.googleAuth.location

import java.io.Serializable

data class Address(
  val addressLines: List<String>? = null,
  val administrativeArea: String? = null,
  val languageCode: String? = null,
  val locality: String? = null,
  val postalCode: String? = null,
  val regionCode: String? = null
) : Serializable