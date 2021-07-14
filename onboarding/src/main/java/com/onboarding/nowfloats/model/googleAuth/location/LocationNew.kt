package com.onboarding.nowfloats.model.googleAuth.location

import java.io.Serializable

data class LocationNew(
  val address: Address? = null,
  val languageCode: String? = null,
  val latlng: Latlng? = null,
  val locationKey: LocationKey? = null,
  val locationName: String? = null,
  val locationState: LocationState? = null,
  val metadata: Metadata? = null,
  val name: String? = null,
  val openInfo: OpenInfo? = null,
  val primaryCategory: PrimaryCategory? = null,
  val primaryPhone: String? = null,
  val serviceArea: ServiceArea? = null,
  val additionalPhones: ArrayList<String>? = null,
  val profile: Any? = null,
  val storeCode: String? = null,
  val websiteUrl: String? = null
) : Serializable